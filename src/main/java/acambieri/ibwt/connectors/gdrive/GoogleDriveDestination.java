package acambieri.ibwt.connectors.gdrive;

import acambieri.ibwt.connectors.DestinationConnector;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.*;

/**
 * @author andrea AC
 *         Date: 29/09/2016
 */
public class GoogleDriveDestination implements DestinationConnector {

    String backupRootFolderId;
    String currentBackupId;

    

    public Drive getDriveService() throws IOException {
        return DriveService.getInstance().getGDrive();
    }
    /*
     * The backup root folder has a special GDrive appProperty "backupRootFolder"
     * Backups folders are tagged as "backupFolder" in the GDrive appProperties
     * Backups files are tagged as "backupFile" in the GDrive appProperties
     */
    public GoogleDriveDestination(String backupDir,String basePathSuffix) throws IOException{
        // root directory for backups  check/create
        FileList files = searchFile("name = '" + backupDir +"' and mimeType = 'application/vnd.google-apps.folder' and appProperties has {key='backupRootFolder' and value='backupRootFolder'}");
        if(files.getFiles().isEmpty()){
            com.google.api.services.drive.model.File backupRoot = new com.google.api.services.drive.model.File();
            backupRoot.setName(backupDir);
            backupRoot.setMimeType("application/vnd.google-apps.folder");
            Map<String,String> appProperties = new HashMap<>();
            appProperties.put("backupRootFolder","backupRootFolder");
            backupRoot.setAppProperties(appProperties);
            com.google.api.services.drive.model.File file = getDriveService().files().create(backupRoot)
                    .setFields("id")
                    .execute();
            backupRootFolderId=file.getId();
    
        }
        else{
            backupRootFolderId = files.getFiles().get(0).getId();
        }
        //current backup dir check/create
        files = searchFile("name = '" + basePathSuffix +"' and mimeType = 'application/vnd.google-apps.folder' and '"+backupRootFolderId+"' in parents and appProperties has {key='backupFolder' and value='backupFolder'} ");
        if(files.getFiles().isEmpty()){
            com.google.api.services.drive.model.File backupFolder = new com.google.api.services.drive.model.File();
            backupFolder.setName(basePathSuffix);
            backupFolder.setMimeType("application/vnd.google-apps.folder");
            Map<String,String> appProperties = new HashMap<>();
            appProperties.put("backupFolder","backupFolder");
            backupFolder.setAppProperties(appProperties);
            backupFolder.setParents(Collections.singletonList(backupRootFolderId));
            com.google.api.services.drive.model.File file = getDriveService().files().create(backupFolder)
                    .setFields("id")
                    .execute();
            currentBackupId=file.getId();
        }
        else{
            currentBackupId=files.getFiles().get(0).getId();
        }
    }
    
    @Override
    public boolean uploadFile(File f) {
        com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
        body.setName(f.getName());
        body.setParents(Collections.singletonList(currentBackupId));
        body.setMimeType("file/zip");
        Map<String,String> appProperties = new HashMap<>();
        appProperties.put("backupFile","backupFile");
        body.setAppProperties(appProperties);
        FileContent content = new FileContent("file/zip",f);
        try {
            com.google.api.services.drive.model.File file = getDriveService().files().create(body,content)
                    .setFields("id")
                    .execute();
        }
        catch(IOException ex){
            LoggerFactory.getLogger(getClass()).error("I/O Exception while uploading file: "  + ex.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean removeFile(File f) {
        try {
            String id;
            if(f instanceof GDriveFile){
                id=((GDriveFile) f).getId();
            }
            else {
                FileList files = searchFile("name='" + f.getName() + "' and '"+backupRootFolderId+"' in parents and appProperties has {key='backupFile' and value='backupFile'} ");
                id=files.getFiles().get(0).getId();
            }
            getDriveService().files().delete(id).execute();
            return true;
        }
        catch(IOException ex){
            LoggerFactory.getLogger(getClass()).error("I/O Exception while removing file: " + ex.getMessage());
            return false;
        }
    }
    
    private FileList searchFile(String query) throws IOException{
        return getDriveService().files().list().setQ(query)
                .setFields("files(id, name)").execute();
    }

    @Override
    public List<File> listBackups() {
        FileList files = null;
        try {
            files = searchFile("mimeType = 'application/vnd.google-apps.folder' and '"+backupRootFolderId+"' in parents and appProperties has {key='backupFolder' and value='backupFolder'} ");
        } catch (IOException e) {
            LoggerFactory.getLogger(getClass()).error("I/O Exception while searching backups: " + e.getMessage());
            return null;
        }
        List<File> result = new ArrayList<>();
        for (com.google.api.services.drive.model.File f : files.getFiles()){
            GDriveFile file = new GDriveFile(f.getName());
            file.setId(f.getId());
            result.add(file);
        }
        result.sort(new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return result;
    }

    @Override
    public boolean removeBackup(File f) {
        try {
            FileList files = searchFile("name = '" + f.getName() + "' and mimeType = 'application/vnd.google-apps.folder' and '" + backupRootFolderId + "' in parents and appProperties has {key='backupFolder' and value='backupFolder'} ");
            FileList dirContent = searchFile("'" + files.getFiles().get(0).getId() + "' in parents and appProperties has {key='backupFile' and value='backupFile'} ");
            for(com.google.api.services.drive.model.File fi : dirContent.getFiles()){
                GDriveFile gf = new GDriveFile(fi.getName());
                gf.setId(fi.getId());
                removeFile(gf);
            }
            getDriveService().files().delete(files.getFiles().get(0).getId()).execute();
        }
        catch(IOException ex){
            LoggerFactory.getLogger(getClass()).error("I/O Exception while removing backup dir " + f.getName() + ": " + ex.getMessage());
            return false;
        }
        return true;
    }

}
