package de.mnbn.opencms.ui.sync;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import org.apache.commons.logging.Log;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsUser;
import org.opencms.main.CmsLog;
import org.opencms.main.OpenCms;
import org.opencms.ui.A_CmsUI;
import org.opencms.ui.CmsVaadinUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;


public class SyncToolOptionsPanel extends VerticalLayout {

    private static final Log LOG = CmsLog.getLog(SyncToolOptionsPanel.class);

    public static final String PROPERTY_SITE_CAPTION = "caption";

    private SyncStatus syncStatus = new SyncStatus();

    private CmsUser m_user;

    private Button cancelButton;
    private Button syncLiveButton;
    private Button syncPreviewButton;

    private Panel outputPanel;
    private Label logOutput;

    private Property<String> logData;

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

        logOutput.addAttachListener(new AttachListener() {
            public void attach(AttachEvent event) {
                logOutput.getUI().setPollInterval(1000);
            }
        });

        OpenCms.getExecutor().scheduleWithFixedDelay(new Runnable() {
            public void run() {
                if (Files.exists(syncStatus.getLogFile())) {
                    logOutput.setValue(readLogContent(syncStatus.getLogFile()).toString());
                }
            }
        }, 1000, 1000, TimeUnit.MILLISECONDS);

        if (syncStatus.isRunning()) {
            Notification.show("Sync wird ausgeführt", "Es wird aktuell ein Sync ausgeführt, bitte warten Sie!",
                    Notification.Type.WARNING_MESSAGE);
        }
    }

    private void sync(SyncCommandKey key) {
        try {
            outputPanel.setVisible(true);

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
        site = cms.getRequestContext().getSiteRoot();
        siteSelector.setValue(site);
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

    private CharSequence readLogContent(Path logFile) {
        try {
            BufferedReader reader = Files.newBufferedReader(logFile, Charset.defaultCharset());
            StringBuilder content = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("<br />");
            }

            return content;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
