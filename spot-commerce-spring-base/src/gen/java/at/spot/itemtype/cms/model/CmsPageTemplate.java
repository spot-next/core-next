/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.cms.model;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.core.infrastructure.type.LocalizedString;

import at.spot.core.model.media.ImageMedia;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;


@ItemType(typeCode = "cmspagetemplate")
@Entity
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class CmsPageTemplate extends AbstractCmsContainerComponent {
    private static final long serialVersionUID = -1L;
    @Property
    protected ImageMedia favIcon;
    @Property
    protected LocalizedString favIconPath;
    @Property
    @ElementCollection
    protected List<MetaTag> metaTags;
    @Property
    protected LocalizedString title;

    @GetProperty
    public ImageMedia getFavIcon() {
        return this.favIcon;
    }

    @GetProperty
    public LocalizedString getFavIconPath() {
        return this.favIconPath;
    }

    @GetProperty
    public List<MetaTag> getMetaTags() {
        return this.metaTags;
    }

    @GetProperty
    public LocalizedString getTitle() {
        return this.title;
    }

    @SetProperty
    public void setFavIcon(ImageMedia favIcon) {
        this.favIcon = favIcon;
        markAsDirty("favIcon");
    }

    @SetProperty
    public void setFavIconPath(LocalizedString favIconPath) {
        this.favIconPath = favIconPath;
        markAsDirty("favIconPath");
    }

    @SetProperty
    public void setMetaTags(List<MetaTag> metaTags) {
        this.metaTags = metaTags;
        markAsDirty("metaTags");
    }

    @SetProperty
    public void setTitle(LocalizedString title) {
        this.title = title;
        markAsDirty("title");
    }
}
