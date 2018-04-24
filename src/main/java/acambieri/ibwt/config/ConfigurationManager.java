package acambieri.ibwt.config;

import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author andrea AC
 *         Date: 14/09/2016
 */
public class ConfigurationManager {

    public void load(){
        if(!Configuration.getIstance().getConfigFile().canRead()){
            throw new RuntimeException("Can't read config file " + Configuration.getIstance().getConfigFile().getAbsolutePath());
        }
        try(BufferedReader reader = new BufferedReader(new FileReader(Configuration.getIstance().getConfigFile()));){
            String line;
            String[] tokens;
            while((line = reader.readLine()) != null){
                if(line.trim().startsWith("#") || line.trim().isEmpty()){
                    continue;
                }
                tokens = line.split("=");
                if(tokens.length==0 || tokens.length > 2){
                    LoggerFactory.getLogger(getClass()).warn("Ignoring [ " + line + " ],format not recognized");
                }
                else{
                    ConfigTokens token = ConfigTokens.valueOf(tokens[0]);
                    tokens[1] = tokens[1].substring(0,tokens[1].indexOf("#") == -1 ? tokens[1].length() : tokens[1].indexOf("#") );
                    if(token == ConfigTokens.BACKUPENTRY){
                        Configuration.getIstance().getBackupList().add(new File(tokens[1]));
                    }
                    else {
                        Class[] parttype = new Class[1];
                        parttype[0] = String.class;
                        try {
                            Method meth = Configuration.class.getMethod("set"+ token.getProperty().substring(0,1).toUpperCase() + token.getProperty().substring(1), parttype);
                            Object[] args = new Object[1];
                            args[0]=tokens[1];
                            meth.invoke(Configuration.getIstance(),args);
                        }
                        catch (NoSuchMethodException ex){
                            throw new RuntimeException("Bug: no property found for token " + token.name());
                        }
                        catch(IllegalAccessException | InvocationTargetException ex){
                            throw new RuntimeException("Error setting property for token " + token.name());
                        }
                    }
                }
            }
        }
        catch (IOException ex){
            throw new RuntimeException("Error reading config file " + Configuration.getIstance().getConfigFile().getAbsolutePath());
        }
    }

    public void createExampleConfig(){
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("acambieri/ibwt/config/config.example")));
             BufferedWriter writer = new BufferedWriter(new FileWriter(Configuration.getIstance().getConfigFile()))) {
            String line;
            while((line = reader.readLine()) != null){
                writer.write(line+"\n");
            }
        }
        catch(IOException ex){
            throw new RuntimeException("Error writing the configuration file " + Configuration.getIstance().getConfigFile().getAbsolutePath());
        }
    }

}
