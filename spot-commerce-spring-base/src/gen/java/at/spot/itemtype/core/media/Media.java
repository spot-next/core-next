/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.core.media;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.persistence.Entity;


@ItemType(typeCode = "media")
@Entity
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Media extends AbstractMedia {
    private static final long serialVersionUID = -1L;

    /** The content of the data object. */
    @Property
    protected byte[] data;

    @GetProperty
    public byte[] getData() {
        return this.data;
    }

    @SetProperty
    public void setData(byte[] data) {
        this.data = data;
        markAsDirty("data");
    }
}
