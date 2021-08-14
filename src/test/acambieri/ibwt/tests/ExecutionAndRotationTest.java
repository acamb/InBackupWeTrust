/*
import acambieri.ibwt.BackupManager;
import acambieri.ibwt.config.Configuration;
import acambieri.ibwt.connectors.ConnectorFactory;
import acambieri.ibwt.connectors.DestinationConnector;
import acambieri.ibwt.executionchecker.ExecutionChecker;
import acambieri.ibwt.executionchecker.FileExecutionChecker;
import org.junit.Assert;
import org.junit.Test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.temporal.ChronoUnit;

*
 * @author andrea AC
 *         Date: 22/09/2016


public class ExecutionAndRotationTest {

    @Test
    public void checkTimeStamps(){
        Configuration.getIstance().setLockFile("lockfile.test");
        Configuration.getIstance().getLockFile().delete();
        Configuration.getIstance().setNextScheduleAmount("1");
        Configuration.getIstance().setNextScheduleUnit("DAYS");
        ExecutionChecker ex = new FileExecutionChecker(Configuration.getIstance().getLockFile());
        ex.updateLastExec();
        //first time: must be != 0+1Day
        try (BufferedReader reader = new BufferedReader(new FileReader("lockfile.test"))){
            long expected = Long.parseLong(reader.readLine());
            Assert.assertEquals(expected,ex.getLastExec());
            Assert.assertFalse(expected == (ChronoUnit.DAYS.getDuration().toMillis()));
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }
        //second time: must be lastexec + 1 DAY
        ex.updateLastExec();
        try (BufferedReader reader = new BufferedReader(new FileReader("lockfile.test"));){
            long expected = Long.parseLong(reader.readLine());
            Assert.assertEquals(expected,ex.getLastExec());
            Assert.assertTrue((ex.getNextSchedule()/1000) == ((expected+ChronoUnit.DAYS.getDuration().toMillis())/1000));
        }
        catch(IOException e){
            throw new RuntimeException(e);
        }

    }


    @Test
    public void checkRotationsIncremental(){
        setupConfigForRotations();
        Configuration.getIstance().setIncremental("TRUE");
        BackupManager manager = new BackupManager();
        DestinationConnector connector = ConnectorFactory.getConnector(Configuration.getIstance().getBackupMethod());
        Assert.assertEquals(6,connector.listBackups().size() );
        manager.run();
        Assert.assertEquals(1,connector.listBackups().size() );
    }

    @Test
    public void checkRotationsNormal(){
        setupConfigForRotations();
        Configuration.getIstance().setIncremental("FALSE");
        BackupManager manager = new BackupManager();
        DestinationConnector connector = ConnectorFactory.getConnector(Configuration.getIstance().getBackupMethod());
        Assert.assertEquals(6,connector.listBackups().size() );
        manager.run();
        Assert.assertEquals(5,connector.listBackups().size());
    }

    private void setupConfigForRotations(){
        Configuration.getIstance().setLockFile("lockfile.test");
        Configuration.getIstance().getLockFile().delete();
        Configuration.getIstance().setBackupDir("test_bckDir");
        Configuration.getIstance().setRotation("5");
        Configuration.getIstance().setBackupMethod("LOCAL");
        Configuration.getIstance().setDaemon("FALSE");
        //File bckDir=new File(Configuration.getIstance().getBackupDir());
        DestinationConnector connector = ConnectorFactory.getConnector(Configuration.getIstance().getBackupMethod());
        //if(connector.listBackups().size() > 1) {
            for (File f : connector.listBackups().subList(0,connector.listBackups().size())){
                connector.removeBackup(f);
            }
       // }
        for(int i=0;i<5;i++){
            File f = new File(Configuration.getIstance().getBackupDir()+ "/"+"00000000"+i);
            f.mkdir();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
*/
