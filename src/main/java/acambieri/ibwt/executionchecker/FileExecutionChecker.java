package acambieri.ibwt.executionchecker;

import acambieri.ibwt.config.Configuration;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author andrea AC
 *         Date: 14/09/2016
 */
public class FileExecutionChecker implements ExecutionChecker {

    private File lockFile;
    private long lastExec;
    private long nextSchedule;

    public FileExecutionChecker(File lockFile){
        if(lockFile.exists()) {
            if (!lockFile.canRead()) {
                LoggerFactory.getLogger(getClass()).error("can't read lock file " + lockFile.getAbsolutePath());
                throw new RuntimeException("can't read lock file " + lockFile.getAbsolutePath());
            }
            if (!lockFile.canWrite()) {
                LoggerFactory.getLogger(getClass()).error("can't write lock file " + lockFile.getAbsolutePath());
                throw new RuntimeException("can't write lock file " + lockFile.getAbsolutePath());
            }
            this.lockFile = lockFile;
            try{
                FileReader file;
                BufferedReader reader = new BufferedReader(file=new FileReader(lockFile));
                lastExec = Long.parseLong(reader.readLine());
                reader.close();
                file.close();
                LocalDateTime date = LocalDateTime.from(new Date(lastExec).toInstant().atZone(ZoneId.systemDefault()));
                this.nextSchedule = date.plus(Configuration.getIstance().getNextScheduleAmount(), Configuration.getIstance().getNextScheduleUnit()).atZone(ZoneId.systemDefault()).toEpochSecond()*1000;
            } catch (IOException ex) {
                LoggerFactory.getLogger(getClass()).error("Error reading lock file " + lockFile.getAbsolutePath());
                throw new RuntimeException("Error reading lock file " + lockFile.getAbsolutePath());
            } catch (NumberFormatException ex) {
                LoggerFactory.getLogger(getClass()).error("Lock file " + lockFile.getAbsolutePath() + " is corrupted,full backup forced!");
                lastExec=0;
            }
        }
        else{
            this.lockFile = lockFile;
            lastExec=0;
            this.nextSchedule=0;
        }

    }

    @Override
    public boolean executionNeeded() {
        return nextSchedule < new Date().toInstant().atZone(ZoneId.systemDefault()).toEpochSecond()*1000;
    }

    @Override
    public long getLastExec() {
        return lastExec;
    }

    @Override
    public boolean incrementalNeeded(long lastModified) {
        return lastModified > lockFile.lastModified();
    }

    @Override
    public void updateLastExec() {
        if(lastExec == 0){
            nextSchedule= new Date().toInstant().atZone(ZoneId.systemDefault()).toEpochSecond()*1000;
        }
        try{
            FileWriter file;
            BufferedWriter writer = new BufferedWriter(file=new FileWriter(lockFile));
            writer.write(String.valueOf(lastExec));
            writer.close();
            file.close();
        }
        catch(IOException ex){
            LoggerFactory.getLogger(getClass()).error("can't update timestamp on lock file " + lockFile.getAbsolutePath() + ", time of last backup will be lost!");
        }
        lastExec=nextSchedule;
        LocalDateTime date = LocalDateTime.from(new Date(lastExec).toInstant().atZone(ZoneId.systemDefault()));
        nextSchedule = date.plus(Configuration.getIstance().getNextScheduleAmount(), Configuration.getIstance().getNextScheduleUnit()).atZone(ZoneId.systemDefault()).toEpochSecond()*1000;
        long now = (new Date()).toInstant().atZone(ZoneId.systemDefault()).toEpochSecond()*1000;
        if(nextSchedule < now){
            LocalDateTime dateNow = LocalDateTime.from(new Date(now).toInstant().atZone(ZoneId.systemDefault()));
            nextSchedule= dateNow.plus(Configuration.getIstance().getNextScheduleAmount(), Configuration.getIstance().getNextScheduleUnit()).atZone(ZoneId.systemDefault()).toEpochSecond()*1000;
        }
    }

    @Override
    public long getNextSchedule() {
        return nextSchedule;
    }
}
