package de.mnbn.opencms.ui.sync;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import org.opencms.file.CmsGroup;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsUser;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsRole;
import org.opencms.ui.apps.A_CmsWorkplaceAppConfiguration;
import org.opencms.ui.apps.CmsAppVisibilityStatus;
import org.opencms.ui.apps.I_CmsAppUIContext;
import org.opencms.ui.apps.I_CmsWorkplaceApp;
import org.opencms.ui.components.OpenCmsTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by schrader on 16.06.16.
 */
public class SyncAppConfiguration extends A_CmsWorkplaceAppConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(SyncAppConfiguration.class);

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
            CmsUser currentUser = cms.getRequestContext().getCurrentUser();
            List<CmsGroup> groups = cms.getGroupsOfUser(currentUser.getName(), true);
            for (CmsGroup group : groups) {
                if (group.getName().endsWith("_sync")) {
                    LOG.debug("Access granted, user {} is in '*_sync' group: {}", currentUser.getName(),
                            group.getName());
                    visibilityStatus = CmsAppVisibilityStatus.ACTIVE;
                }
            }

            if (!visibilityStatus.isActive()) {
                LOG.debug("No access to sync app for user {}, the doesn't belong to a group which ends with: '_sync'",
                        currentUser.getName());
            }
        } catch (CmsException e) {
            throw new RuntimeException(e);
        }
        return visibilityStatus;
    }
}
