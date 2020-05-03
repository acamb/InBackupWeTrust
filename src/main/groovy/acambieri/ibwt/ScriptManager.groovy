package acambieri.ibwt

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ScriptManager {
    private File scriptsDir
    static final Logger log = LoggerFactory.getLogger(ScriptManager.class)

    ScriptManager(String scriptsDir){
        this.scriptsDir = new File(scriptsDir)
        if(!this.scriptsDir.exists() || !this.scriptsDir.canRead()){
            throw new RuntimeException("Can't access to ${this.scriptsDir.absolutePath}, check permissions/paths!")
        }
    }

    void runScripts(){
        log.info("Running scripts from ${scriptsDir.absolutePath}")
        scriptsDir.eachFileMatch(~/.*\.groovy/){ file ->
            log.info("Executing ${file.name}...")
            GroovyShell shell = new GroovyShell()
            try {
                def script = shell.parse(file)
                script.runScript()
            }
            catch(Exception ex){
                log.error("Error executing ${file.name}: ${ex.getMessage()}")
            }
            finally {
                log.info("${file.name} finished")
            }
        }
    }
}
