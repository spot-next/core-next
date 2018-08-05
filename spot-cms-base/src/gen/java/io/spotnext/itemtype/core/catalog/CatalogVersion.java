/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.core.catalog;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.infrastructure.annotation.Relation;

import io.spotnext.itemtype.core.UniqueIdItem;
import io.spotnext.itemtype.core.catalog.Catalog;
import io.spotnext.itemtype.core.catalog.CatalogVersion;
import io.spotnext.itemtype.core.internationalization.Language;

import java.io.Serializable;

import java.lang.String;

import java.util.Set;


/**
 * A catalog version holds catalogable types that can be synchronized into another catalog version.
 */
@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "catalogversion")
public class CatalogVersion extends UniqueIdItem {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "catalogversion";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_SYNCHRONIZATION_TARGET = "synchronizationTarget";
    public static final String PROPERTY_SYNCHRONIZATION_LANGUAGES = "synchronizationLanguages";
    public static final String PROPERTY_CATALOG = "catalog";

    /**
     * The name of the catalog version.
     */
    @Property(readable = true, writable = true)
    protected String name;

    /**
     * The target catalog version to which all containing items will be synchronized..
     */
    @Property(readable = true, writable = true)
    protected CatalogVersion synchronizationTarget;

    /**
     * The languages that will be synchronized .
     */
    @Property(readable = true, writable = true)
    protected Set<Language> synchronizationLanguages;
    @Property(readable = true, initial = false, unique = true, writable = true)
    @Relation(relationName = "Catalog2CatalogVersion", mappedTo = "versions", type = io.spotnext.core.infrastructure.type.RelationType.ManyToOne, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.TARGET)
    public Catalog catalog;

    /**
     * The target catalog version to which all containing items will be synchronized..
     */
    @Accessor(propertyName = "synchronizationTarget", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setSynchronizationTarget(CatalogVersion synchronizationTarget) {
        this.synchronizationTarget = synchronizationTarget;
    }

    /**
     * The languages that will be synchronized .
     */
    @Accessor(propertyName = "synchronizationLanguages", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setSynchronizationLanguages(
        Set<Language> synchronizationLanguages) {
        this.synchronizationLanguages = synchronizationLanguages;
    }

    /**
     * The name of the catalog version.
     */
    @Accessor(propertyName = "name", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getName() {
        return this.name;
    }

    /**
     * The name of the catalog version.
     */
    @Accessor(propertyName = "name", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setName(String name) {
        this.name = name;
    }

    @Accessor(propertyName = "catalog", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Catalog getCatalog() {
        return this.catalog;
    }

    /**
     * The languages that will be synchronized .
     */
    @Accessor(propertyName = "synchronizationLanguages", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<Language> getSynchronizationLanguages() {
        return this.synchronizationLanguages;
    }

    @Accessor(propertyName = "catalog", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    /**
     * The target catalog version to which all containing items will be synchronized..
     */
    @Accessor(propertyName = "synchronizationTarget", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public CatalogVersion getSynchronizationTarget() {
        return this.synchronizationTarget;
    }
}
