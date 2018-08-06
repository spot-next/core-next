/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.commerce.catalog;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.infrastructure.annotation.Relation;

import io.spotnext.itemtype.commerce.catalog.Product;
import io.spotnext.itemtype.core.UniqueIdItem;
import io.spotnext.itemtype.core.internationalization.LocalizedString;

import java.io.Serializable;

import java.lang.String;

import java.util.Set;


/**
 * Categories are used to group products.
 */
@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "category")
public class Category extends UniqueIdItem {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "category";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_DESCRIPTION = "description";
    public static final String PROPERTY_PRODUCTS = "products";
    @Property(readable = true, writable = true)
    protected String name;
    @Property(readable = true, writable = true)
    protected LocalizedString description;

    /**
     * The categories the product is referenced by.
     */
    @Relation(collectionType = io.spotnext.core.infrastructure.type.RelationCollectionType.Set, relationName = "Category2Product", mappedTo = "categories", type = io.spotnext.core.infrastructure.type.RelationType.ManyToMany, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.SOURCE)
    @Property(readable = true, writable = true)
    public Set<Product> products;

    @Accessor(propertyName = "name", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getName() {
        return this.name;
    }

    @Accessor(propertyName = "description", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public LocalizedString getDescription() {
        return this.description;
    }

    /**
     * The categories the product is referenced by.
     */
    @Accessor(propertyName = "products", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<Product> getProducts() {
        return this.products;
    }

    @Accessor(propertyName = "name", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setName(String name) {
        this.name = name;
    }

    @Accessor(propertyName = "description", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setDescription(LocalizedString description) {
        this.description = description;
    }

    /**
     * The categories the product is referenced by.
     */
    @Accessor(propertyName = "products", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setProducts(Set<Product> products) {
        this.products = products;
    }
}
