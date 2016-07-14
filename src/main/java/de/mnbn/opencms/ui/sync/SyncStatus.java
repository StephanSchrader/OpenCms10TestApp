package de.mnbn.opencms.ui.sync;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by schrader on 17.06.16.
 */
public class SyncStatus {

    public boolean isRunning() {
        String pid = System.getProperty("sync.pid", "/tmp/sync.pid");
        return  Files.exists(Paths.get(pid));
    }

    public Path getLogFile() {
        String log = System.getProperty("sync.log", "/tmp/sync.log");
        return Paths.get(log);
    }

}
