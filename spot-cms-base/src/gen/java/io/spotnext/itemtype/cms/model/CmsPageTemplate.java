/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.cms.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.model.media.ImageMedia;

import io.spotnext.itemtype.cms.model.AbstractCmsContainerComponent;
import io.spotnext.itemtype.core.internationalization.LocalizedString;

import java.io.Serializable;

import java.lang.String;

import java.util.Set;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "cmspagetemplate")
public class CmsPageTemplate extends AbstractCmsContainerComponent {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "cmspagetemplate";
    public static final String PROPERTY_TITLE = "title";
    public static final String PROPERTY_FAV_ICON_PATH = "favIconPath";
    public static final String PROPERTY_FAV_ICON = "favIcon";
    public static final String PROPERTY_META_TAGS = "metaTags";
    @Property(readable = true, writable = true)
    protected LocalizedString title;
    @Property(readable = true, writable = true)
    protected LocalizedString favIconPath;
    @Property(readable = true, writable = true)
    protected ImageMedia favIcon;
    @Property(readable = true, writable = true)
    protected Set<String> metaTags;

    @Accessor(propertyName = "favIconPath", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public LocalizedString getFavIconPath() {
        return this.favIconPath;
    }

    @Accessor(propertyName = "title", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public LocalizedString getTitle() {
        return this.title;
    }

    @Accessor(propertyName = "metaTags", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<String> getMetaTags() {
        return this.metaTags;
    }

    @Accessor(propertyName = "metaTags", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setMetaTags(Set<String> metaTags) {
        this.metaTags = metaTags;
    }

    @Accessor(propertyName = "title", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setTitle(LocalizedString title) {
        this.title = title;
    }

    @Accessor(propertyName = "favIconPath", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setFavIconPath(LocalizedString favIconPath) {
        this.favIconPath = favIconPath;
    }

    @Accessor(propertyName = "favIcon", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public ImageMedia getFavIcon() {
        return this.favIcon;
    }

    @Accessor(propertyName = "favIcon", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setFavIcon(ImageMedia favIcon) {
        this.favIcon = favIcon;
    }
}
