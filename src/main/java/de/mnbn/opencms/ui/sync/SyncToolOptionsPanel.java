package de.mnbn.opencms.ui.sync;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import org.apache.commons.logging.Log;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsUser;
import org.opencms.main.CmsLog;
import org.opencms.ui.A_CmsUI;
import org.opencms.ui.CmsVaadinUtils;


public class SyncToolOptionsPanel extends VerticalLayout {

    private static final Log LOG = CmsLog.getLog(SyncToolOptionsPanel.class);

    public static final String PROPERTY_SITE_CAPTION = "caption";

    private CmsUser m_user;

    private Button cancelButton;
    private Button syncLiveButton;
    private Button syncPreviewButton;

    //private Panel outputPanel;
    private Label logOutput;

    private ComboBox siteSelector;

    private String site;

    public SyncToolOptionsPanel() {
        CmsVaadinUtils.readAndLocalizeDesign(this, CmsVaadinUtils.getWpMessagesForCurrentLocale(), null);

        m_user = A_CmsUI.getCmsObject().getRequestContext().getCurrentUser();

        syncLiveButton.addClickListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                sync(SyncCommandKey.SYNC_PROD);
            }
        });
        syncPreviewButton.addClickListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                sync(SyncCommandKey.SYNC_PREVIEW);
            }
        });
        cancelButton.addClickListener(new ClickListener() {

            private static final long serialVersionUID = 1L;

            public void buttonClick(ClickEvent event) {
                A_CmsUI.get().getPage().setLocation(CmsVaadinUtils.getWorkplaceLink());
            }
        });

        siteSelector = createSiteSelect(A_CmsUI.getCmsObject());

        //outputPanel.getUI().setVisible(true);
        logOutput.addAttachListener(new AttachListener() {
            public void attach(AttachEvent event) {
                event.getConnector().getUI().setPollInterval(250);
            }
        });

        //LogRunnable logRunnable = new LogRunnable("/tmp/the-big-logfile.log", logOutput);
        //new Thread(logRunnable).start();
    }

    private void sync(SyncCommandKey key) {
        try {
            new SyncCommand(A_CmsUI.getCmsObject())
                    .site(site)
                    .command(key)
                    .call();

            Notification.show("Sync gestartet", Notification.Type.HUMANIZED_MESSAGE);
        } catch (Exception e) {
            LOG.error("Sync failed", e);
            Notification.show("Sync failed: '" + e.getMessage() + "'", Notification.Type.ERROR_MESSAGE);
        }
    }

    private ComboBox createSiteSelect(CmsObject cms) {

        siteSelector.setContainerDataSource(CmsVaadinUtils.getAvailableSitesContainer(cms, PROPERTY_SITE_CAPTION));
        String siteRoot = cms.getRequestContext().getSiteRoot();
        siteSelector.setValue(siteRoot);
        siteSelector.setNullSelectionAllowed(false);
        siteSelector.setItemCaptionPropertyId(PROPERTY_SITE_CAPTION);
        siteSelector.setFilteringMode(FilteringMode.CONTAINS);
        siteSelector.addValueChangeListener(new Property.ValueChangeListener() {

            /** Serial version id. */
            private static final long serialVersionUID = 1L;

            public void valueChange(Property.ValueChangeEvent event) {
                site = (String)(event.getProperty().getValue());
            }
        });

        return siteSelector;
    }

}
