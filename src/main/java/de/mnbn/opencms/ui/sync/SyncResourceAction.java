package de.mnbn.opencms.ui.sync;

import org.apache.commons.logging.Log;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsLog;
import org.opencms.ui.I_CmsDialogContext;
import org.opencms.ui.actions.A_CmsWorkplaceAction;
import org.opencms.workplace.explorer.menu.CmsMenuItemVisibilityMode;

import java.util.List;

/**
 * Created by schrader on 16.06.16.
 */
public class SyncResourceAction extends A_CmsWorkplaceAction {

    private static final Log LOG = CmsLog.getLog(SyncResourceAction.class);

    private SyncCommandKey command;

    public SyncResourceAction(SyncCommandKey command) {
        this.command = command;
    }

    public void executeAction(I_CmsDialogContext context) {
        try {
            new SyncCommand(context.getCms())
                    .command(SyncCommandKey.SYNC_PREVIEW)
                    .resources(context.getResources())
                    .call();
        } catch (Exception e) {
            LOG.error("Sync failed", e);
            context.error(e);
        }
    }

    public String getId() {
        return "syncAction";
    }

    public String getTitle() {
        return "Sync - " + command;
    }

    public CmsMenuItemVisibilityMode getVisibility(CmsObject cms, List<CmsResource> resources) {
        return CmsMenuItemVisibilityMode.VISIBILITY_ACTIVE;
    }

}
