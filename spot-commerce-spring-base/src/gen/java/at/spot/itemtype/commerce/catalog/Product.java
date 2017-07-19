/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.commerce.catalog;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.core.infrastructure.type.LocalizedString;
import at.spot.core.infrastructure.type.RelationType;

import at.spot.itemtype.core.UniqueIdItem;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;


/**
* The base type Product is used for all purchasable items.
 */
@ItemType(typeCode = "product")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Product extends UniqueIdItem {
    private static final long serialVersionUID = -1L;

    /** The categories the product is referenced by. */
    @Property
    @Relation(type = RelationType.ManyToMany, mappedTo = "products", referencedType = Category.class)
    protected List<Category> categories;

    /** The localized description of the product. */
    @Property
    protected LocalizedString description;

    /** The EAN product code. */
    @Property
    protected String ean;

    /** The name of the product. */
    @Property
    protected String name;

    @GetProperty
    public List<Category> getCategories() {
        return this.categories;
    }

    @GetProperty
    public LocalizedString getDescription() {
        return this.description;
    }

    @GetProperty
    public String getEan() {
        return this.ean;
    }

    @GetProperty
    public String getName() {
        return this.name;
    }

    @SetProperty
    public void setCategories(List<Category> categories) {
        this.categories = categories;
        markAsDirty("categories");
    }

    @SetProperty
    public void setDescription(LocalizedString description) {
        this.description = description;
        markAsDirty("description");
    }

    @SetProperty
    public void setEan(String ean) {
        this.ean = ean;
        markAsDirty("ean");
    }

    @SetProperty
    public void setName(String name) {
        this.name = name;
        markAsDirty("name");
    }
}
