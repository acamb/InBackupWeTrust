package acambieri.ibwt.engines;

import acambieri.ibwt.executionchecker.ExecutionChecker;

/**
 * @author andrea AC
 *         Date: 01/03/2017
 */
public class CompressionEngineFactory {
    
    public static CompressionEngine getEngine(CompressionMethod method,ExecutionChecker checker){
        switch(method){
            case ZIP :
                return new ZipEngine() {
                    @Override
                    public ExecutionChecker getExecutionChecker() {
                        return checker;
                    }
                };
            case XZ:
                return new XZEngine(){
                    @Override
                    public ExecutionChecker getExecutionChecker() {
                        return checker;
                    }
                };
            default:
                throw new RuntimeException("Compression method not supported!");
        }
    }
}
