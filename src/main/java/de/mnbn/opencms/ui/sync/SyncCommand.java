package de.mnbn.opencms.ui.sync;

import org.apache.commons.collections.ExtendedProperties;
import org.opencms.file.CmsGroup;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.security.CmsRole;
import org.opencms.ui.A_CmsUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Created by schrader on 16.06.16.
 */
public class SyncCommand implements Callable<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(SyncCommand.class);

    private List<CmsResource> resources;

    private String site;

    private CmsObject cms;

    private SyncCommandKey command;

    private ExtendedProperties properties;

    public SyncCommand(CmsObject cms) {
        this.cms = cms;

        try {
            String syncCommandsFile = System.getProperty("sync.commands", "/tmp/sync-commands.properties");
            LOG.debug("Loading sync commands from: '{}'", syncCommandsFile);

            properties = new ExtendedProperties(syncCommandsFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Void call() throws Exception {
        if (!isAllowedToSyncSite()) {
            throw new SyncNotPermittedException();
        }

        String name = Objects.requireNonNull(command.name(), "command");
        String rawScriptCall = Objects.requireNonNull(properties.getString(name), "command from properties");

        String scriptCall = String.format(rawScriptCall, getBrand(), getSite());
        LOG.info("Trying to execute script: '{}', startedBy: {}", scriptCall, cms.getRequestContext().getCurrentUser());

        Runtime.getRuntime().exec(scriptCall);

        return null;
    }

    private String getSite() {
        if (site == null || site.isEmpty() || site.equals("/")) {
            return "";
        }

        return CmsResource.getName(site);
    }

    private String getBrand() {
        String brand;

        try {
            CmsObject cmsObject = OpenCms.initCmsObject(cms);
            cmsObject.getRequestContext().setSiteRoot(site);

            CmsProperty brandProperty = cmsObject.readPropertyObject("/", "brand", false);
            brand = brandProperty.getValue("unknown");

            LOG.debug("Current brand: '{}', site: '{}'", brand, site);
        } catch (CmsException e) {
            throw new RuntimeException(e);
        }

        return brand;
    }


    private boolean isAllowedToSyncSite() {
        CmsObject cms = A_CmsUI.getCmsObject();
        if (OpenCms.getRoleManager().hasRole(cms, CmsRole.ROOT_ADMIN)) {
            // der Root Admin darf immer alles, wirklich immer und alles!
            return true;
        }

        boolean isAllowed = false;

        String brand = getBrand();
        String syncGroup = properties.getString("brand." + brand + ".syncGroup");
        if (syncGroup != null) {
            isAllowed = isInGroup(cms, syncGroup);
        }

        LOG.debug("User allowed [{}] to sync, needed group: '{}'", cms.getRequestContext().getCurrentUser(), syncGroup);

        return isAllowed;
    }

    public SyncCommand command(SyncCommandKey command) {
        this.command = Objects.requireNonNull(command, "command");
        return this;
    }

    public SyncCommand resources(List<CmsResource> resources) {
        this.resources = Objects.requireNonNull(resources, "resources");
        return this;
    }

    public SyncCommand site(String site) {
        this.site = site;
        return this;
    }

    private static boolean isInGroup(CmsObject cms, String group) {
        try {
            List<CmsGroup> groups = cms.getGroupsOfUser(cms.getRequestContext().getCurrentUser().getName(), true);
            for (CmsGroup cmsGroup : groups) {
                if (group.equals(cmsGroup.getSimpleName())) {
                    return true;
                }
            }

        } catch (CmsException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
}
