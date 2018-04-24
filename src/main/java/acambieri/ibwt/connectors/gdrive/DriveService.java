package acambieri.ibwt.connectors.gdrive;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author andrea AC
 *         Date: 01/03/2017
 */
public class DriveService {
    
    private static DriveService me;
    private Drive drive;
    
    /** Application name. */
    private static final String APPLICATION_NAME =
            "InBackupWeTrust";
    
    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
            System.getProperty("user.home"), ".credentials/InBackupWeTrust");
    
    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;
    
    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
            JacksonFactory.getDefaultInstance();
    
    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;
    
    /** Global instance of the scopes */
    private static final List<String> SCOPES =
            Arrays.asList(DriveScopes.DRIVE);
    
    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            if(!DATA_STORE_DIR.exists()){
                DATA_STORE_DIR.mkdir();
            }
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
    
    private static Credential authorize() throws IOException {
        InputStream in;
        try {
            in = new FileInputStream(DATA_STORE_DIR.getAbsoluteFile() + "/client.json");
        }
        catch(FileNotFoundException ex){
            LoggerFactory.getLogger(GoogleDriveDestination.class).error("Please save client.json generated file in " + DATA_STORE_DIR.getAbsoluteFile().getAbsolutePath());
            throw ex;
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                        .setDataStoreFactory(DATA_STORE_FACTORY)
                        .setAccessType("offline")
                        .build();
        Credential credential = new AuthorizationCodeInstalledApp(
                flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }
    
    private DriveService() throws IOException {
        Credential credential = authorize();
        drive= new Drive.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }
    
    public static DriveService getInstance(){
        if(me == null){
            synchronized (DriveService.class){
                if(me == null){
                    try {
                        me = new DriveService();
                    } catch (IOException e) {
                        //TODO[AC]
                        e.printStackTrace();
                    }
                }
            }
        }
        return me;
    }
    
    public Drive getGDrive(){
        return drive;
    }
}
