package acambieri.ibwt.executionchecker;

import java.util.Date;

/**
 * @author andrea AC
 *         Date: 09/09/2016
 */
public interface ExecutionChecker {

    public boolean executionNeeded();

    public long getLastExec();

    public boolean incrementalNeeded(long lastModified);

    public void updateLastExec();

    public long getNextSchedule();
}
