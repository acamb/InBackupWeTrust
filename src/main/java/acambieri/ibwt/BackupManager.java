package acambieri.ibwt;

import acambieri.ibwt.config.Configuration;
import acambieri.ibwt.connectors.ConnectorFactory;
import acambieri.ibwt.connectors.DestinationConnector;
import acambieri.ibwt.connectors.FilesystemDestination;
import acambieri.ibwt.engines.CompressionEngine;
import acambieri.ibwt.engines.CompressionEngineFactory;
import acambieri.ibwt.engines.CompressionMethod;
import acambieri.ibwt.engines.ZipEngine;
import acambieri.ibwt.executionchecker.ExecutionChecker;
import acambieri.ibwt.executionchecker.FileExecutionChecker;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author andrea AC
 *         Date: 09/09/2016
 */
public class BackupManager implements Runnable{

    private volatile boolean shutdown=false;

    @Override
    public void run() {
        ExecutionChecker checker = new FileExecutionChecker(Configuration.getIstance().getLockFile());
        do{
            List<File> oldFiles=null;
            //Used only to force a full backup in incremental mode
            boolean fullBackupNeeded=false;
            if(Configuration.getIstance().isDaemon() || checker.executionNeeded()){
                DestinationConnector destination = ConnectorFactory.getConnector(Configuration.getIstance().getBackupMethod());
                //Remove the older rotation if needed
                if(destination.listBackups().size() > Configuration.getIstance().getRotation()){
                    if(!Configuration.getIstance().isIncremental()) {
                        while(destination.listBackups().size() > Configuration.getIstance().getRotation()) {
                            destination.removeFile(destination.listBackups().get(0));
                        }
                    }
                    else{
                        oldFiles=destination.listBackups();
                        oldFiles=oldFiles.subList(0,oldFiles.size()-1);
                        fullBackupNeeded=true;
                    }
                }
                if(Configuration.getIstance().isEnableScripts()){
                    try {
                        ScriptManager scriptManager = new ScriptManager(Configuration.getIstance().getPreBackupScriptsDir());
                        scriptManager.runScripts();
                    }
                    catch(Exception ex){
                        LoggerFactory.getLogger(getClass()).error("Error running pre-backup scripts:" + ex.getMessage());
                    }
                }
                //In this way we create a compressed file for each backup entry
                for (File f : Configuration.getIstance().getBackupList()){
                    CompressionEngine compressor = CompressionEngineFactory.getEngine(Configuration.getIstance().getCompressionMethod(),checker);
                    compressor.init(f,fullBackupNeeded);
                    File tmpFile = new File(Configuration.getIstance().getTmpFolder().getAbsolutePath() +
                            "/" +
                            f.toURI().getPath().replaceAll(":","").replaceAll("/","_") +
                            Configuration.getIstance().getCompressionMethod().getFileExtension());
                    compressor.compress(tmpFile);
                    destination.uploadFile(tmpFile);
                    tmpFile.delete();
                }
                checker.updateLastExec();
                //remove files older than the current full backup
                if(fullBackupNeeded){
                    for (File f : oldFiles){
                        //this is recursive
                        destination.removeBackup(f);
                    }
                }
            }
            if(Configuration.getIstance().isDaemon() && !shutdown){
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {

                }
            }
        }while(Configuration.getIstance().isDaemon() && !shutdown);
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }
}
