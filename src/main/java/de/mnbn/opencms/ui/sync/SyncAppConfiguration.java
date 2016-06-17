package de.mnbn.opencms.ui.sync;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import org.opencms.file.CmsObject;
import org.opencms.ui.apps.A_CmsWorkplaceAppConfiguration;
import org.opencms.ui.apps.CmsAppVisibilityStatus;
import org.opencms.ui.apps.I_CmsAppUIContext;
import org.opencms.ui.apps.I_CmsWorkplaceApp;
import org.opencms.ui.components.OpenCmsTheme;

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
        return super.getVisibility(cms);
    }
}
