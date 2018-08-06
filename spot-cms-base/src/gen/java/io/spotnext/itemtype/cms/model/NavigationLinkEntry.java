/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.cms.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;

import io.spotnext.itemtype.cms.enumeration.HtmlLinkTarget;
import io.spotnext.itemtype.cms.model.AbstractNavigationEntry;

import java.io.Serializable;

import java.lang.String;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "navigationlinkentry")
public class NavigationLinkEntry extends AbstractNavigationEntry {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "navigationlinkentry";
    public static final String PROPERTY_HREF = "href";
    public static final String PROPERTY_TARGET = "target";

    /**
     * The href of the link.
     */
    @Property(readable = true, writable = true)
    protected String href;

    /**
     * The HTML link target.
     */
    @Property(readable = true, writable = true)
    protected HtmlLinkTarget target;

    /**
     * The href of the link.
     */
    @Accessor(propertyName = "href", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getHref() {
        return this.href;
    }

    /**
     * The HTML link target.
     */
    @Accessor(propertyName = "target", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setTarget(HtmlLinkTarget target) {
        this.target = target;
    }

    /**
     * The href of the link.
     */
    @Accessor(propertyName = "href", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * The HTML link target.
     */
    @Accessor(propertyName = "target", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public HtmlLinkTarget getTarget() {
        return this.target;
    }
}
