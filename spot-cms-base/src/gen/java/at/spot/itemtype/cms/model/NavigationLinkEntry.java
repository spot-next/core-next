/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.cms.model;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.itemtype.cms.enumeration.HtmlLinkTarget;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@ItemType(typeCode = "navigationlinkentry")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class NavigationLinkEntry extends AbstractNavigationEntry {
    private static final long serialVersionUID = -1L;

    /** The href of the link. */
    @Property
    protected String href;

    /** The HTML link target. */
    @Property
    protected HtmlLinkTarget target;

    @GetProperty
    public String getHref() {
        return this.href;
    }

    @GetProperty
    public HtmlLinkTarget getTarget() {
        return this.target;
    }

    @SetProperty
    public void setHref(String href) {
        this.href = href;
        markAsDirty("href");
    }

    @SetProperty
    public void setTarget(HtmlLinkTarget target) {
        this.target = target;
        markAsDirty("target");
    }
}
