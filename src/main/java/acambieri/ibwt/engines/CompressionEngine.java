package acambieri.ibwt.engines;

import acambieri.ibwt.executionchecker.ExecutionChecker;

import java.io.File;

/**
 * @author andrea AC
 *         Date: 14/09/2016
 */
public interface CompressionEngine {


    public boolean compress(File output);
    
    public void init(File root,boolean fullBackupNeeded);
    
    public ExecutionChecker getExecutionChecker();
}
