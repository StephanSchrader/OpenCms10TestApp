package de.mnbn.opencms.ui.sync;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.logging.Log;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsLog;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Created by schrader on 16.06.16.
 */
public class SyncCommand implements Callable<Void> {

    private static final Log LOG = CmsLog.getLog(SyncCommand.class);

    private List<CmsResource> resources;

    private String site;

    private CmsObject cms;

    private SyncCommandKey command;

    private ExtendedProperties properties;

    public SyncCommand(CmsObject cms) {
        this.cms = cms;

        try {
            String syncCommandsFile = System.getProperty("sync.commands", "/tmp/sync-commands.properties");
            properties = new ExtendedProperties(syncCommandsFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Void call() throws Exception {
        String name = Objects.requireNonNull(command.name(), "command");
        String rawScriptCall = Objects.requireNonNull(properties.getString(name), "command from properties");

        String scriptCall = String.format(rawScriptCall, site);

        Process process = Runtime.getRuntime().exec(scriptCall);
        //process.waitFor();

        //LOG.info("Executed command '" + command + "', return value: '" + process.exitValue() + "'");
        LOG.info("Executed command '" + command);

        return null;
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
}
