/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.core.catalog;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.infrastructure.annotation.Relation;
import io.spotnext.core.infrastructure.support.ItemCollectionFactory;

import io.spotnext.itemtype.core.UniqueIdItem;
import io.spotnext.itemtype.core.catalog.CatalogVersion;

import java.io.Serializable;

import java.lang.String;

import java.util.Set;


/**
 * This is the container for all category and product-related types. It can have multiple catalog version.
 */
@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "catalog")
public class Catalog extends UniqueIdItem {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "catalog";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_VERSIONS = "versions";

    /**
     * The name of the catalog.
     */
    @Property(readable = true, writable = true)
    protected String name;
    @Property(readable = true, writable = true)
    @Relation(collectionType = io.spotnext.core.infrastructure.type.RelationCollectionType.List, relationName = "Catalog2CatalogVersion", mappedTo = "catalog", type = io.spotnext.core.infrastructure.type.RelationType.OneToMany, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.SOURCE)
    public Set<CatalogVersion> versions;

    /**
     * The name of the catalog.
     */
    @Accessor(propertyName = "name", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setName(String name) {
        this.name = name;
    }

    @Accessor(propertyName = "versions", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<CatalogVersion> getVersions() {
        return ItemCollectionFactory.wrap(this, "versions", this.versions);
    }

    /**
     * The name of the catalog.
     */
    @Accessor(propertyName = "name", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getName() {
        return this.name;
    }

    @Accessor(propertyName = "versions", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setVersions(Set<CatalogVersion> versions) {
        this.versions = versions;
    }
}
