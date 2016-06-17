package de.mnbn.opencms.staticexport;

import org.opencms.db.CmsPublishedResource;
import org.opencms.file.CmsObject;
import org.opencms.file.CmsResource;
import org.opencms.main.CmsException;
import org.opencms.main.OpenCms;
import org.opencms.staticexport.CmsAfterPublishStaticExportHandler;
import org.opencms.staticexport.CmsStaticExportExportRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Copy from org.opencms.staticexport.CmsAfterPublishStaticExportHandler
 */
public class AfterPublishStaticExportHandler extends CmsAfterPublishStaticExportHandler {

    @Override
    protected List<CmsPublishedResource> getRelatedResources(CmsObject cms, List<CmsPublishedResource> publishedResources) throws CmsException {

        String storedSiteRoot = cms.getRequestContext().getSiteRoot();
        try {
            // switch to root site
            cms.getRequestContext().setSiteRoot("/");
            if (publishedResources == null) {
                // full static export
                return this.getAllResources(cms);
            } else {
                // after publish export
                Map<String, CmsPublishedResource> resourceMap = new HashMap<String, CmsPublishedResource>();
                Iterator<CmsPublishedResource> itPubRes = publishedResources.iterator();
                while (itPubRes.hasNext()) {
                    CmsPublishedResource pubResource = itPubRes.next();
                    // check the internal flag if the resource does still exist
                    // we cannot export with an internal flag
                    if (cms.existsResource(pubResource.getRootPath())) {
                        CmsResource vfsResource = cms.readResource(pubResource.getRootPath());
                        if (!vfsResource.isInternal()) {
                            // add only if not internal
                            // additionally, add all siblings of the resource
                            Iterator<CmsPublishedResource> itSiblings = this.getSiblings(cms, pubResource).iterator();
                            while (itSiblings.hasNext()) {
                                CmsPublishedResource sibling = itSiblings.next();
                                resourceMap.put(sibling.getRootPath(), sibling);
                            }
                        }
                    } else {
                        // the resource does not exist, so add them for deletion in the static export
                        resourceMap.put(pubResource.getRootPath(), pubResource);
                    }

                    boolean match = false;
                    Iterator<CmsStaticExportExportRule> itExportRules = OpenCms.getStaticExportManager().getExportRules().iterator();
                    while (itExportRules.hasNext()) {
                        CmsStaticExportExportRule rule = itExportRules.next();
                        Set<CmsPublishedResource> relatedResources = rule.getRelatedResources(cms, pubResource);
                        if (relatedResources != null) {
                            Iterator<CmsPublishedResource> itRelatedRes = relatedResources.iterator();
                            while (itRelatedRes.hasNext()) {
                                CmsPublishedResource relatedRes = itRelatedRes.next();
                                resourceMap.put(relatedRes.getRootPath(), relatedRes);
                            }
                            match = true;
                        }
                    }
                    // if one res does not match any rule, then export all files
                    if (!match) {
                        // workaround, was: getAllResources(cms)
                        return publishedResources;
                    }
                }
                return new ArrayList<CmsPublishedResource>(resourceMap.values());
            }
        } finally {
            cms.getRequestContext().setSiteRoot(storedSiteRoot);
        }
    }
}
