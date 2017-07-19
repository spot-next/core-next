/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.core.user;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.core.model.Item;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.validation.constraints.NotNull;


@ItemType(typeCode = "address")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Address extends Item {
    private static final long serialVersionUID = -1L;

    /** The owner of this address. */
    @Property(unique = true)
    @NotNull
    protected Item owner;

    /** The address type of this address. */
    @Property(unique = true)
    @NotNull
    protected AddressType type;

    @GetProperty
    public Item getOwner() {
        return this.owner;
    }

    @GetProperty
    public AddressType getType() {
        return this.type;
    }

    @SetProperty
    public void setOwner(Item owner) {
        this.owner = owner;
        markAsDirty("owner");
    }

    @SetProperty
    public void setType(AddressType type) {
        this.type = type;
        markAsDirty("type");
    }
}
