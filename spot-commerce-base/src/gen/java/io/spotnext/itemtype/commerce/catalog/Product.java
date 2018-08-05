/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.commerce.catalog;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.infrastructure.annotation.Relation;

import io.spotnext.itemtype.commerce.catalog.Category;
import io.spotnext.itemtype.core.UniqueIdItem;
import io.spotnext.itemtype.core.internationalization.LocalizedString;

import java.io.Serializable;

import java.lang.String;

import java.util.Set;


/**
 * The base type Product is used for all purchasable items.
 */
@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "product")
public class Product extends UniqueIdItem {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "product";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_DESCRIPTION = "description";
    public static final String PROPERTY_EAN = "ean";
    public static final String PROPERTY_CATEGORIES = "categories";

    /**
     * The name of the product.
     */
    @Property(readable = true, writable = true)
    protected String name;

    /**
     * The localized description of the product.
     */
    @Property(readable = true, writable = true)
    protected LocalizedString description;

    /**
     * The EAN product code.
     */
    @Property(readable = true, writable = true)
    protected String ean;

    /**
     * The categories the product is referenced by.
     */
    @Property(readable = true, writable = true)
    @Relation(collectionType = io.spotnext.core.infrastructure.type.RelationCollectionType.Set, relationName = "Category2Product", mappedTo = "products", type = io.spotnext.core.infrastructure.type.RelationType.ManyToMany, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.TARGET)
    public Set<Category> categories;

    /**
     * The EAN product code.
     */
    @Accessor(propertyName = "ean", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setEan(String ean) {
        this.ean = ean;
    }

    /**
     * The name of the product.
     */
    @Accessor(propertyName = "name", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setName(String name) {
        this.name = name;
    }

    /**
     * The localized description of the product.
     */
    @Accessor(propertyName = "description", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public LocalizedString getDescription() {
        return this.description;
    }

    /**
     * The localized description of the product.
     */
    @Accessor(propertyName = "description", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setDescription(LocalizedString description) {
        this.description = description;
    }

    /**
     * The categories the product is referenced by.
     */
    @Accessor(propertyName = "categories", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    /**
     * The name of the product.
     */
    @Accessor(propertyName = "name", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getName() {
        return this.name;
    }

    /**
     * The EAN product code.
     */
    @Accessor(propertyName = "ean", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getEan() {
        return this.ean;
    }

    /**
     * The categories the product is referenced by.
     */
    @Accessor(propertyName = "categories", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<Category> getCategories() {
        return this.categories;
    }
}
