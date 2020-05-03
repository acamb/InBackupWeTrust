package acambieri.ibwt.config;

import acambieri.ibwt.engines.CompressionMethod;

import java.io.File;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author andrea AC
 *         Date: 09/09/2016
 */
public class Configuration {

    private File lockFile = new File(System.getProperty("user.home") + "/ibwt.lock");
    private File configFile = new File(System.getProperty("user.home") + "/ibwt.cfg");
    List<File> backupList;
    private boolean incremental;
    HashMap<File,List<File>> failures;
    private int rotation;
    private static Configuration me;
    private int nextScheduleAmount=1;
    private TemporalUnit nextScheduleUnit= ChronoUnit.DAYS;
    private boolean daemon=false;
    private String backupDir="backups";
    private File tmpFolder;
    private BackupMethod backupMethod;
    private CompressionMethod compressionMethod = CompressionMethod.ZIP;
    private boolean enableScripts;
    private String preBackupScriptsDir;

    private Configuration(){
        backupList = new ArrayList<>();
    }

    public static Configuration getIstance(){
        if(me == null){
            synchronized (Configuration.class){
                if(me == null){
                    me = new Configuration();
                }
            }
        }
        return me;
    }

    public File getLockFile() {
        return lockFile;
    }

    public void setLockFile(String lockFile) {
        this.lockFile = new File(lockFile);
    }

    public File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public List<File> getBackupList() {
        return backupList;
    }

    public void setBackupList(List<File> backupList) {
        this.backupList = backupList;
    }

    public boolean isIncremental() {
        return incremental;
    }

    public void setIncremental(String incremental) {
        if(incremental == null){
            this.incremental=false;
        }
        else {
            this.incremental = incremental.equalsIgnoreCase("true");
        }
    }

    public HashMap<File, List<File>> getFailures() {
        return failures;
    }

    public void setFailures(HashMap<File, List<File>> failures) {
        this.failures = failures;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(String rotation) {
        if(rotation == null){
            this.rotation=0;
        }
        else {
            try {
                this.rotation = Integer.parseInt(rotation);
            }
            catch(NumberFormatException ex){
                throw new RuntimeException(ConfigTokens.ROTATIONS.name() + "="+rotation + " - is not a valid number");
            }
        }
    }

    public int getNextScheduleAmount() {
        return nextScheduleAmount;
    }

    public void setNextScheduleAmount(String nextScheduleAmount) {
        try {
            this.nextScheduleAmount = Integer.parseInt(nextScheduleAmount);
        }
        catch(Exception ex){
            throw new RuntimeException(ConfigTokens.FREQUENCY.name() + "="+nextScheduleAmount + " - invalid entry format");
        }
    }

    public TemporalUnit getNextScheduleUnit() {
        return nextScheduleUnit;
    }

    public void setNextScheduleUnit(String nextScheduleUnit) {
        try {
            this.nextScheduleUnit = ChronoUnit.valueOf(nextScheduleUnit);
        }
        catch(IllegalArgumentException | NullPointerException ex){
            throw new RuntimeException(ConfigTokens.FREQUENCY_UNIT+ " must be one of the following values: [ " + Arrays.toString(ChronoUnit.values()) + "]" );
        }
    }

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(String daemon) {
        if(daemon == null){
            this.daemon=false;
        }
        else {
            this.daemon = daemon.equalsIgnoreCase("true");
        }
    }

    public String getBackupDir() {
        return backupDir;
    }

    public void setBackupDir(String backupDir) {
        this.backupDir = backupDir;
    }

    public void setTmpFolder(String folder){
        File f = new File(folder);
        if(!f.exists()){
            f.mkdir();
        }
        if(!f.isDirectory() || !f.canRead() || !f.canWrite()){
            throw new RuntimeException("TMPFOLDER must be a directory, readable and writeable");
        }
        tmpFolder=f;
    }

    public File getTmpFolder() {
        return tmpFolder;
    }

    public void setBackupMethod(String method){
        try {
            backupMethod = BackupMethod.valueOf(method);
        }
        catch(IllegalArgumentException ex){
            throw new RuntimeException("BACKUPMETHOD=" + method + " isn't a valid value");
        }
    }

    public BackupMethod getBackupMethod() {
        return backupMethod;
    }
    
    public void setCompressionMethod(String method){
        try{
            compressionMethod = CompressionMethod.valueOf(method);
        }
        catch(IllegalArgumentException ex){
            throw new RuntimeException("COMPRESSIONMETHOD=" + method + " isn't a valid value");
        }
    }
    
    public CompressionMethod getCompressionMethod() {
        return compressionMethod;
    }

    public boolean isEnableScripts() {
        return enableScripts;
    }

    public void setEnableScripts(String enableScripts) {
        this.enableScripts = enableScripts != null && "true".equals(enableScripts.toLowerCase());
    }

    public String getPreBackupScriptsDir() {
        return preBackupScriptsDir;
    }

    public void setPreBackupScriptsDir(String preBackupScriptsDir) {
        this.preBackupScriptsDir = preBackupScriptsDir;
    }
}
