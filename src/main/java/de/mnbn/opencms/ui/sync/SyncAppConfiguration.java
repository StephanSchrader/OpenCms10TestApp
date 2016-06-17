package de.mnbn.opencms.ui.sync;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import org.opencms.file.CmsGroup;
import org.opencms.file.CmsObject;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsRole;
import org.opencms.ui.apps.A_CmsWorkplaceAppConfiguration;
import org.opencms.ui.apps.CmsAppVisibilityStatus;
import org.opencms.ui.apps.I_CmsAppUIContext;
import org.opencms.ui.apps.I_CmsWorkplaceApp;
import org.opencms.ui.components.OpenCmsTheme;

import java.util.List;

/**
 * Created by schrader on 16.06.16.
 */
public class SyncAppConfiguration extends A_CmsWorkplaceAppConfiguration {

    @Override
    public String getAppCategory() {
        return "Main";
    }

    public I_CmsWorkplaceApp getAppInstance() {
        return new I_CmsWorkplaceApp() {

            public void initUI(I_CmsAppUIContext context) {
                context.setAppContent(new SyncToolOptionsPanel());
                context.showInfoArea(false);
            }

            public void onStateChange(String state) {
                // ??
            }
        };
    }

    public Resource getIcon() {
        return new ExternalResource(OpenCmsTheme.getImageLink("apps/sync-icon.png"));
    }

    public String getId() {
        return "sync";
    }

    @Override
    public CmsAppVisibilityStatus getVisibility(CmsObject cms) {

        if (OpenCms.getRoleManager().hasRole(cms, CmsRole.ROOT_ADMIN)) {
            // der Root Admin darf immer alles, wirklich immer und alles!
            return CmsAppVisibilityStatus.ACTIVE;
        }

        CmsAppVisibilityStatus visibilityStatus = CmsAppVisibilityStatus.INVISIBLE;
        try {
            // alle user, die in einer gruppe sind, die '_sync' endet haben zugriff auf die app
            List<CmsGroup> groups = cms.getGroupsOfUser(cms.getRequestContext().getCurrentUser().getName(), true);
            for (CmsGroup group : groups) {
                if (group.getName().endsWith("_sync")) {
                    visibilityStatus = CmsAppVisibilityStatus.ACTIVE;
                }
            }
        } catch (CmsException e) {
            throw new RuntimeException(e);
        }
        return visibilityStatus;
    }
}
