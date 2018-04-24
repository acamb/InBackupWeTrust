package acambieri.ibwt.engines;

/**
 * @author andrea AC
 *         Date: 01/03/2017
 */
public enum CompressionMethod {
    ZIP(".zip"),
    XZ(".tar.xz");
    
    private String fileExtension;
    
    CompressionMethod(String fileExtension){
        this.fileExtension=fileExtension;
    }
    
    public String getFileExtension(){
        return fileExtension;
    }
}
