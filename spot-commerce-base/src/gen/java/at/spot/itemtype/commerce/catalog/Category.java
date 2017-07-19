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
* Categories are used to group products.
 */
@ItemType(typeCode = "category")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Category extends UniqueIdItem {
    private static final long serialVersionUID = -1L;
    @Property
    protected LocalizedString description;
    @Property
    protected String name;

    /** The products in this catalog. */
    @Property
    @Relation(type = RelationType.ManyToMany, mappedTo = "categories", referencedType = Product.class)
    protected List<Product> products;

    @GetProperty
    public LocalizedString getDescription() {
        return this.description;
    }

    @GetProperty
    public String getName() {
        return this.name;
    }

    @GetProperty
    public List<Product> getProducts() {
        return this.products;
    }

    @SetProperty
    public void setDescription(LocalizedString description) {
        this.description = description;
        markAsDirty("description");
    }

    @SetProperty
    public void setName(String name) {
        this.name = name;
        markAsDirty("name");
    }

    @SetProperty
    public void setProducts(List<Product> products) {
        this.products = products;
        markAsDirty("products");
    }
}
