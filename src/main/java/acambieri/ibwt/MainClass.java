package acambieri.ibwt;

import acambieri.ibwt.config.Configuration;
import acambieri.ibwt.config.ConfigurationManager;

import java.io.File;

/**
 * @author andrea AC
 *         Date: 09/09/2016
 */
public class MainClass {

    public static void main(String[] args){
        Configuration.getIstance();
        if(args.length > 0){
            Configuration.getIstance().setConfigFile(new File(args[0]));
        }
        ConfigurationManager configManager = new ConfigurationManager();
        if(Configuration.getIstance().getConfigFile().exists()) {
            configManager.load();
            final BackupManager manager = new BackupManager();
            final Thread t = new Thread(manager);
            t.start();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    manager.setShutdown(true);
                    t.interrupt();
                }
            });
            if(Configuration.getIstance().isDaemon()) {
                //This is needed to detach the console
                //System.out.close();
                //System.err.close();
            }
        }
        else{
            configManager.createExampleConfig();
            System.out.println("Example configuration created: " + Configuration.getIstance().getConfigFile().getAbsolutePath());
        }
    }
}
