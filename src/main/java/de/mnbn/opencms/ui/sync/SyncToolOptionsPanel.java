package de.mnbn.opencms.ui.sync;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import org.apache.commons.logging.Log;
import org.opencms.file.CmsUser;
import org.opencms.main.CmsLog;
import org.opencms.ui.A_CmsUI;
import org.opencms.ui.CmsVaadinUtils;


public class SyncToolOptionsPanel extends VerticalLayout {

    private static final Log LOG = CmsLog.getLog(SyncToolOptionsPanel.class);

    private CmsUser m_user;

    private Button cancelButton;
    private Button okButton;

    public SyncToolOptionsPanel() {
        CmsVaadinUtils.readAndLocalizeDesign(this, CmsVaadinUtils.getWpMessagesForCurrentLocale(), null);

        m_user = A_CmsUI.getCmsObject().getRequestContext().getCurrentUser();

        okButton.addClickListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                try {
                    new SyncCommand(A_CmsUI.getCmsObject()).call();
                } catch (Exception e) {
                    LOG.error("Sync failed", e);
                    Notification.show("Sync failed: '" + e.getMessage() + "'", Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        cancelButton.addClickListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                A_CmsUI.get().getPage().setLocation(CmsVaadinUtils.getWorkplaceLink());
            }
        });
    }

}
