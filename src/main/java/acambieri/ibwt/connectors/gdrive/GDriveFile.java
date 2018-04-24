package acambieri.ibwt.connectors.gdrive;

import java.io.File;
import java.net.URI;

/**
 * @author andrea AC
 *         Date: 28/02/2017
 */
public class GDriveFile extends File {
    private String id;
    
    public GDriveFile(String pathname) {
        super(pathname);
    }
    
    public GDriveFile(String parent, String child) {
        super(parent, child);
    }
    
    public GDriveFile(File parent, String child) {
        super(parent, child);
    }
    
    public GDriveFile(URI uri) {
        super(uri);
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
}
