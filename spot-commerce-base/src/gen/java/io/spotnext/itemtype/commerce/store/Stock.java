/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.commerce.store;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.types.Item;

import java.io.Serializable;

import java.lang.Integer;
import java.lang.String;

import javax.validation.constraints.NotNull;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "stock")
public class Stock extends Item {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "stock";
    public static final String PROPERTY_PRODUCT_ID = "productId";
    public static final String PROPERTY_VALUE = "value";
    public static final String PROPERTY_RESERVED = "reserved";
    @Property(readable = true, unique = true, writable = true)
    @NotNull
    protected String productId;

    /**
     * The actual stock level.
     */
    @Property(readable = true, writable = true)
    protected Integer value = 0;

    /**
     * The reserved amount of stock.
     */
    @Property(readable = true, writable = true)
    protected Integer reserved = 0;

    /**
     * The reserved amount of stock.
     */
    @Accessor(propertyName = "reserved", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Integer getReserved() {
        return this.reserved;
    }

    /**
     * The reserved amount of stock.
     */
    @Accessor(propertyName = "reserved", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setReserved(Integer reserved) {
        this.reserved = reserved;
    }

    @Accessor(propertyName = "productId", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getProductId() {
        return this.productId;
    }

    @Accessor(propertyName = "productId", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * The actual stock level.
     */
    @Accessor(propertyName = "value", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Integer getValue() {
        return this.value;
    }

    /**
     * The actual stock level.
     */
    @Accessor(propertyName = "value", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setValue(Integer value) {
        this.value = value;
    }
}
