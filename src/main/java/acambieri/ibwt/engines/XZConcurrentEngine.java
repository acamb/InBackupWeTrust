package acambieri.ibwt.engines;

import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarOutputStream;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class XZConcurrentEngine extends AbstractCompressionEngine {

    private volatile boolean readError=false;
    private volatile boolean writeError=false;

    @Override
    public boolean compress(final File output) {
        final File outputTarFile = new File(output.getAbsolutePath().substring(0,output.getAbsolutePath().length()-3));
        final PipedOutputStream tarOutputStream = new PipedOutputStream();
        final PipedInputStream xzInputStream = new PipedInputStream(16*1024);
        try {
            xzInputStream.connect(tarOutputStream);
        } catch (IOException e) {
            //TODO[AC] handle,log ecc
            return false;
        }
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    TarOutputStream tar = new TarOutputStream(tarOutputStream);
                    for (File file : files) {
                        tar.putNextEntry(new TarEntry(file, relativeRoot.relativize(file.toURI()).getPath()));
                        BufferedInputStream origin = new BufferedInputStream(new FileInputStream(file));
                        copyStreams(origin, tar);
                    }
                    tar.close();
                }
                catch(IOException ex){
                    readError=true;
                }
            }
        });
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    XZOutputStream xzOutputStream = new XZOutputStream(new FileOutputStream(output), new LZMA2Options());
                    InputStream tarFileStream = xzInputStream;
                    copyStreams(tarFileStream, xzOutputStream);
                    xzOutputStream.close();
                }
                catch(IOException ex){
                    writeError=true;
                }
            }
        });
        boolean wait=true;
        while(wait){
            try {
                executor.shutdown();
                wait=!executor.awaitTermination(100, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                //nothing to do
            }
        }
        return !(readError || writeError);
    }
}
