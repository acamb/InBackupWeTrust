package acambieri.ibwt;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Utils {
    public static String exceptionToString(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }
}
