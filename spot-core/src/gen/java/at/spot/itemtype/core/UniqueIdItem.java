/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.core;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.core.model.Item;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.persistence.Entity;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;


/**
* The base type all types that have a uique id property.
 */
@ItemType(typeCode = "uniqueiditem")
@Entity
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class UniqueIdItem extends Item {
    private static final long serialVersionUID = -1L;

    /** The unique id of the object. */
    @Property(unique = true)
    @Length(min = 3)
    @NotNull
    protected String id;

    @GetProperty
    public String getId() {
        return this.id;
    }

    @SetProperty
    public void setId(String id) {
        this.id = id;
        markAsDirty("id");
    }
}
