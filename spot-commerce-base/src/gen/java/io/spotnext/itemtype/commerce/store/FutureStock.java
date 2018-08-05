/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.commerce.store;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;

import io.spotnext.itemtype.commerce.store.Stock;

import java.io.Serializable;

import java.lang.String;

import javax.validation.constraints.NotNull;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "futurestock")
public class FutureStock extends Stock {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "futurestock";
    public static final String PROPERTY_AVAILABLE_AT = "availableAt";

    /**
     * Defines a future stock and when it will be available.
     */
    @NotNull
    @Property(readable = true, writable = true)
    protected String availableAt;

    /**
     * Defines a future stock and when it will be available.
     */
    @Accessor(propertyName = "availableAt", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getAvailableAt() {
        return this.availableAt;
    }

    /**
     * Defines a future stock and when it will be available.
     */
    @Accessor(propertyName = "availableAt", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setAvailableAt(String availableAt) {
        this.availableAt = availableAt;
    }
}
