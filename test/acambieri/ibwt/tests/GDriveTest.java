package acambieri.ibwt.tests;

import acambieri.ibwt.connectors.gdrive.GoogleDriveDestination;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author andrea AC
 *         Date: 14/02/2017
 */
public class GDriveTest {

    @Test
    public void test1(){
        try {
            GoogleDriveDestination destination = new GoogleDriveDestination("backups","20170214");
            destination.uploadFile(new File("prova.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
