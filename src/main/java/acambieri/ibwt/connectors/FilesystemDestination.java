package acambieri.ibwt.connectors;


import jdk.nashorn.internal.parser.DateParser;
import org.slf4j.LoggerFactory;

import javax.naming.directory.BasicAttributes;
import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author andrea AC
 *         Date: 19/09/2016
 */
public class FilesystemDestination implements  DestinationConnector {

    private File basePath;
    private String backupDir;


    /**
     *
     * @param basePathSuffix The new directory name in the backup path (will be created if needed)
     */
    public FilesystemDestination(String backupDir,String basePathSuffix){
        this.basePath=new File(backupDir + "/" + basePathSuffix);
        this.backupDir=backupDir;
        File bckdir = new File(backupDir);
/*        if(!bckdir.exists()){
            bckdir.mkdir();
        }*/
        if(!this.basePath.exists()){
            this.basePath.mkdirs();
        }
        if(!this.basePath.canRead() || !this.basePath.canWrite()){
            throw new RuntimeException("Can't handle backup path " + this.basePath.getAbsolutePath());
        }
    }

    @Override
    public boolean uploadFile(File f) {
        try( FileInputStream input = new FileInputStream(f);
             FileOutputStream output = new FileOutputStream(new File(basePath.getAbsolutePath() + "/" + f.getName())) ){
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            return true;
        }
        catch(IOException ex){
            LoggerFactory.getLogger(getClass()).error("I/O Exception while uploading file: "  + ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeFile(File f) {
        try {
            System.gc();
            return Files.deleteIfExists(f.toPath());
        }
        catch(IOException ex){
            return false;
        }
    }


    public boolean moveFile(File oldFile, File newFile) {
        try {
            Files.move(oldFile.toPath(),newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     *
     * @return An ordered list of backups dirs (oldest first)
     */
    @Override
    public List<File> listBackups() {
        File backupDir = new File(this.backupDir);
        List<File> result = new ArrayList<>();
        Stream<Path> stream=null;
        try  {
            stream = Files.list(backupDir.toPath());
            stream.filter(Files::isDirectory)
                    .filter(s -> s.toFile().getName().matches("[0-9]{8,12}"))
                    .sorted(new Comparator<Path>() {
                        @Override
                        public int compare(Path o1, Path o2) {
                            DateFormat format = new SimpleDateFormat("yyyyMMddhhmm");
                            try {
                                /*Date d1 = format.parse(o1.toFile().getName());
                                Date d2 = format.parse(o2.toFile().getName());*/
                                FileTime d1 = Files.readAttributes(o1, BasicFileAttributes.class).creationTime();
                                FileTime d2 = Files.readAttributes(o2, BasicFileAttributes.class).creationTime();
                                return d1.compareTo(d2);
                            } catch (IOException ex) {
                                return -1;
                            }
                        }
                    }).forEach(s -> result.add(s.toFile()));
            return result;
        }
        catch(IOException ex){
            throw new RuntimeException("can't read backup directory " + backupDir.getAbsolutePath());
        }
        finally {
            if(stream != null){
                stream.close();
            }
        }
    }

    @Override
    public boolean removeBackup(File f) {
        try {
            Stream<Path> stream = Files.list(f.toPath());
            stream.filter(Files::isRegularFile).forEach(p -> p.toFile().delete());
            stream.close();
            stream = Files.list(f.toPath());
            stream.filter(Files::isDirectory).forEach(p -> p.toFile().delete());
            stream.close();
            return f.delete();
        }
        catch(IOException ex) {
            return false;
        }
    }
}
