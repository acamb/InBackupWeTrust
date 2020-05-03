package acambieri.ibwt.config;

/**
 * @author andrea AC
 *         Date: 15/09/2016
 */
public enum ConfigTokens {

    LOCKFILE("lockFile"),
    INCREMENTAL("incremental"),
    ROTATIONS("rotation"),
    FREQUENCY("nextScheduleAmount"),
    FREQUENCY_UNIT("nextScheduleUnit"),
    DAEMON("daemon"),
    BACKUPDIR("backupDir"),
    BACKUPENTRY("backupEntry"),
    TMPFOLDER("tmpFolder"),
    BACKUPMETHOD("backupMethod"),
    COMPRESSIONMETHOD("compressionMethod"),
    ENABLE_SCRIPTS("enableScripts"),
    PRE_BACKUP_SCRIPTS_DIR("preBackupScriptsDir");

    private String property;

    private ConfigTokens(String property){
        this.property=property;
    }

    public String getProperty(){
        if(this.property.equals("backupEntry")){
            return null;
        }
        else{
            return property;
        }
    }


    public ConfigTokens getTokenFor(String property){
        for (ConfigTokens token : ConfigTokens.values()){
            if(token.getProperty().equals(property)){
                return token;
            }
        }
        return null;
    }
}
