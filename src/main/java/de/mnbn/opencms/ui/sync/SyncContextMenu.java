package de.mnbn.opencms.ui.sync;

import org.opencms.ui.actions.CmsContextMenuActionItem;
import org.opencms.ui.actions.CmsEditDialogAction;
import org.opencms.ui.contextmenu.I_CmsContextMenuItem;
import org.opencms.ui.contextmenu.I_CmsContextMenuItemProvider;

import java.util.Arrays;
import java.util.List;

/**
 * Created by schrader on 16.06.16.
 */
public class SyncContextMenu implements I_CmsContextMenuItemProvider {

    public List<I_CmsContextMenuItem> getMenuItems() {
        return Arrays.<I_CmsContextMenuItem>asList(
                new CmsContextMenuActionItem(new SyncResourceAction(), "advanced", 50, 0)
        );
    }

}
