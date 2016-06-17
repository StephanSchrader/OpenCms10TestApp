package de.mnbn.opencms.ui.sync;

import org.apache.commons.logging.Log;
import org.opencms.main.CmsLog;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by schrader on 17.06.16.
 */
public class SyncStatus {

    private static final Log LOG = CmsLog.getLog(SyncStatus.class);

    public boolean isRunning() {
        String pid = System.getProperty("sync.pid", "/tmp/sync.pid");
        return Files.exists(Paths.get(pid));
    }

    public Path getLogFile() {
        String log = System.getProperty("sync.log", "/tmp/sync.log");
        return Paths.get(log);
    }

}
