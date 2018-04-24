package acambieri.ibwt.engines;

import acambieri.ibwt.engines.CompressionEngine;
import acambieri.ibwt.executionchecker.ExecutionChecker;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarOutputStream;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;

import java.io.*;

/**
 * @author andrea AC
 *         Date: 01/03/2017
 */
public abstract class XZEngine extends AbstractCompressionEngine {
    
    @Override
    public boolean compress(File output) {
        File outputTarFile = new File(output.getAbsolutePath().substring(0,output.getAbsolutePath().length()-3));
        try {
            FileOutputStream outFileStream = new FileOutputStream(outputTarFile);
            TarOutputStream tarOutputStream = new TarOutputStream(new BufferedOutputStream(outFileStream));
            for (File file : files) {
                tarOutputStream.putNextEntry(new TarEntry(file, relativeRoot.relativize(file.toURI()).getPath()));
                BufferedInputStream origin = new BufferedInputStream(new FileInputStream(file));
                copyStreams(origin,tarOutputStream);
            }
            tarOutputStream.close();
            XZOutputStream xzOutputStream = new XZOutputStream(new FileOutputStream(output),new LZMA2Options());
            BufferedInputStream tarFileStream = new BufferedInputStream(new FileInputStream(outputTarFile));
            copyStreams(tarFileStream,xzOutputStream);
            xzOutputStream.close();

        }
        catch(IOException ex){
            return false;
        }
        return true;
    }
    
}
