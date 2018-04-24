package acambieri.ibwt.engines;

import acambieri.ibwt.config.Configuration;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * @author andrea AC
 *         Date: 02/03/2017
 */
public abstract class AbstractCompressionEngine implements CompressionEngine {
    
    protected List<File> files;
    protected byte[] buffer = new byte[2048];
    protected boolean fullBackupNeeded;
    protected URI relativeRoot;
    
    @Override
    public void init(File root, boolean fullBackupNeeded) {
        this.fullBackupNeeded=fullBackupNeeded;
        this.files = new ArrayList<>();
        relativeRoot = root.toURI();
        this.files.addAll(listReadableFiles(root));
    }
    
    
    protected List<File> listReadableFiles(File f) {
        List<File> output = new ArrayList<>();
        try {
            Files.walkFileTree(f.toPath(), new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if(Configuration.getIstance().isIncremental() && !fullBackupNeeded){
                        if(getExecutionChecker().incrementalNeeded(file.toFile().lastModified())){
                            output.add(file.toFile());
                        }
                    }
                    else {
                        output.add(file.toFile());
                    }
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    LoggerFactory.getLogger(getClass()).error("Cannot read file " + file.toFile().getAbsolutePath() + ": " + exc.getMessage());
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch(Exception ex){
            LoggerFactory.getLogger(getClass()).error("An error occoured while scanning files: " + ex.getMessage());
        }
        return output;
    }

    protected void copyStreams(InputStream inputStream, OutputStream output) throws IOException{
        /* The old way
        int count;
        byte data[] = new byte[4096];

        while ((count = inputStream.read(data)) != -1) {
            output.write(data, 0, count);
        }

        output.flush();
        inputStream.close();*/

        ReadableByteChannel readChannel = Channels.newChannel(inputStream);
        WritableByteChannel writeChannel = Channels.newChannel(output);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
        while (readChannel.read(buffer) != -1) {
            // prepare the buffer to be drained
            buffer.flip();
            // write to the channel, may block
            writeChannel.write(buffer);
            // If partial transfer, shift remainder down
            // If buffer is empty, same as doing clear()
            buffer.compact();
        }
        // EOF will leave buffer in fill state
        buffer.flip();
        // make sure the buffer is fully drained.
        while (buffer.hasRemaining()) {
            writeChannel.write(buffer);
        }
    }
}
