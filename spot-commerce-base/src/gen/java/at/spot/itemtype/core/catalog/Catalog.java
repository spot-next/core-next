/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.core.catalog;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.itemtype.core.UniqueIdItem;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.persistence.Entity;


/**
* This is the container for all categories and products.
 */
@ItemType(typeCode = "catalog")
@Entity
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Catalog extends UniqueIdItem {
    private static final long serialVersionUID = -1L;

    /** The name of the catalog. */
    @Property
    protected String name;

    @GetProperty
    public String getName() {
        return this.name;
    }

    @SetProperty
    public void setName(String name) {
        this.name = name;
        markAsDirty("name");
    }
}
