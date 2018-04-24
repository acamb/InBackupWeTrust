package acambieri.ibwt.connectors;

import acambieri.ibwt.config.BackupMethod;
import acambieri.ibwt.config.Configuration;
import acambieri.ibwt.connectors.gdrive.GoogleDriveDestination;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

/**
 * @author andrea AC
 *         Date: 20/09/2016
 */
public class ConnectorFactory {

    public static DestinationConnector getConnector(BackupMethod method){

        switch (method){
            case LOCAL:
                return new FilesystemDestination(Configuration.getIstance().getBackupDir(),getDirFormat());
            case DRIVE:
                try {
                    return new GoogleDriveDestination(Configuration.getIstance().getBackupDir(),getDirFormat());
                } catch (IOException e) {
                    LoggerFactory.getLogger(ConnectorFactory.class).error(e.getMessage(),e);
                    throw new RuntimeException("I/O Error creating Google Drive connnector");
                }
        }
        throw new RuntimeException("No DestinationConnector found for backup method: " + method.name());
    }


    public static String getDirFormat(){
        DateFormat format = new SimpleDateFormat("yyyyMMddhhmm");
        return format.format(GregorianCalendar.getInstance().getTime());
    }
}
