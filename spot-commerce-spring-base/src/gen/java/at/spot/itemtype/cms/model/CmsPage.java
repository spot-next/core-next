/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.cms.model;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;


@ItemType(typeCode = "cmspage")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class CmsPage extends CmsPageTemplate {
    private static final long serialVersionUID = -1L;
    @Property
    protected CmsPageTemplate template;

    /** Holds the regex string to determine which URL should be handler by this page. */
    @Property
    protected List<String> urlMatches;

    @GetProperty
    public CmsPageTemplate getTemplate() {
        return this.template;
    }

    @GetProperty
    public List<String> getUrlMatches() {
        return this.urlMatches;
    }

    @SetProperty
    public void setTemplate(CmsPageTemplate template) {
        this.template = template;
        markAsDirty("template");
    }

    @SetProperty
    public void setUrlMatches(List<String> urlMatches) {
        this.urlMatches = urlMatches;
        markAsDirty("urlMatches");
    }
}
