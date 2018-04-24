package acambieri.ibwt.engines;

import acambieri.ibwt.config.Configuration;
import acambieri.ibwt.executionchecker.ExecutionChecker;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author andrea AC
 *         Date: 14/09/2016
 */
public abstract class ZipEngine extends AbstractCompressionEngine {
    
    @Override
    public boolean compress(File output) {
        try {
            FileOutputStream out = new FileOutputStream(output);
            ZipOutputStream zip = new ZipOutputStream(out);
            //TODO[AC] compressionLevel
            //zip.setLevel(Configuration.getIstance().getCo);
            for(File f : files){
                ZipEntry entry = new ZipEntry(relativeRoot.relativize(f.toURI()).getPath());
                zip.putNextEntry(entry);
                FileInputStream is = new FileInputStream(f.getAbsolutePath());
                int len;
                while ((len = is.read(buffer)) > 0) {
                    zip.write(buffer, 0, len);
                }
                is.close();
                zip.closeEntry();
            }

            zip.close();
        }
        catch(IOException ex){
            return false;
        }
        return true;
    }
    
}
