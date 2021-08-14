import acambieri.ibwt.ScriptManager
import org.junit.Test

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class ScriptManagerTest{

    @Test
    public void testScript(){
        ScriptManager manager = new ScriptManager("../../../backedStore/backupScripts")
        manager.runScripts()
    }
}