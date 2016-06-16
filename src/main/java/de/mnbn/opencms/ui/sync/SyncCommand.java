package de.mnbn.opencms.ui.sync;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by schrader on 16.06.16.
 */
public class SyncCommand implements Callable<Void> {

    private List<CmsResource> resources;

    private CmsObject cms;

    public SyncCommand(CmsObject cms) {
        this.cms = cms;
    }

    public Void call() throws Exception {
        Runtime.getRuntime().exec("/home/user/sync.sh");
        return null;
    }

    public SyncCommand resources(List<CmsResource> resources) {
        this.resources = resources;
        return this;
    }
}
