/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.core;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.types.Item;

import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

import java.lang.String;

import javax.validation.constraints.NotNull;


/**
 * The base type of all types that have a unique id property.
 */
@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = false, typeCode = "uniqueiditem")
public abstract class UniqueIdItem extends Item {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "uniqueiditem";
    public static final String PROPERTY_ID = "id";

    /**
     * The unique id of the object.
     */
    @Length(min = 3)
    @Property(readable = true, unique = true, writable = true)
    @NotNull
    protected String id;

    /**
     * The unique id of the object.
     */
    @Accessor(propertyName = "id", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setId(String id) {
        this.id = id;
    }

    /**
     * The unique id of the object.
     */
    @Accessor(propertyName = "id", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getId() {
        return this.id;
    }
}
