package acambieri.ibwt.connectors;

import java.io.File;
import java.util.List;

/**
 * @author andrea AC
 *         Date: 16/09/2016
 */
public interface DestinationConnector {

    public boolean uploadFile(File f);

    public boolean removeFile(File f);
    
    public List<File> listBackups();

    public boolean removeBackup(File f);

}
