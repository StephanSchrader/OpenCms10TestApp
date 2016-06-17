package de.mnbn.opencms.ui.sync;

/**
 * Created by schrader on 17.06.16.
 */
public class SyncNotPermittedException extends RuntimeException {

    public SyncNotPermittedException(String message) {
        super(message);
    }

    public SyncNotPermittedException() {
    }
}
