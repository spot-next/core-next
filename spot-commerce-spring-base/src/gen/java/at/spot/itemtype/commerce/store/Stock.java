/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.commerce.store;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.core.model.Item;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.persistence.Entity;

import javax.validation.constraints.NotNull;


@ItemType(typeCode = "stock")
@Entity
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Stock extends Item {
    private static final long serialVersionUID = -1L;
    @Property(unique = true)
    @NotNull
    protected String productId;

    /** The reserved amount of stock. */
    @Property
    protected int reserved = 0;

    /** The actual stock level. */
    @Property
    protected int value = 0;

    @GetProperty
    public String getProductId() {
        return this.productId;
    }

    @GetProperty
    public int getReserved() {
        return this.reserved;
    }

    @GetProperty
    public int getValue() {
        return this.value;
    }

    @SetProperty
    public void setProductId(String productId) {
        this.productId = productId;
        markAsDirty("productId");
    }

    @SetProperty
    public void setReserved(int reserved) {
        this.reserved = reserved;
        markAsDirty("reserved");
    }

    @SetProperty
    public void setValue(int value) {
        this.value = value;
        markAsDirty("value");
    }
}
