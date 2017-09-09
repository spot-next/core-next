/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.core.media;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.itemtype.core.UniqueIdItem;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;


@ItemType(typeCode = "mediacontainer")
@Entity
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class MediaContainer extends UniqueIdItem {
    private static final long serialVersionUID = -1L;

    /** A list of all related media objects.. */
    @Property
    @ElementCollection
    protected List<AbstractMedia> medias;

    @GetProperty
    public List<AbstractMedia> getMedias() {
        return this.medias;
    }

    @SetProperty
    public void setMedias(List<AbstractMedia> medias) {
        this.medias = medias;
        markAsDirty("medias");
    }
}
