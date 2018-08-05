/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.core;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;

import io.spotnext.itemtype.core.UniqueIdItem;
import io.spotnext.itemtype.core.catalog.CatalogVersion;

import java.io.Serializable;

import java.lang.String;

import javax.validation.constraints.NotNull;


/**
 * The base type all types that are catalogable.
 */
@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = false, typeCode = "catalogitem")
public abstract class CatalogItem extends UniqueIdItem {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "catalogitem";
    public static final String PROPERTY_CATALOG_VERSION = "catalogVersion";

    /**
     * The catalog version this item belongs to.
     */
    @Property(readable = true, unique = true, writable = true)
    @NotNull
    protected CatalogVersion catalogVersion;

    /**
     * The catalog version this item belongs to.
     */
    @Accessor(propertyName = "catalogVersion", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public CatalogVersion getCatalogVersion() {
        return this.catalogVersion;
    }

    /**
     * The catalog version this item belongs to.
     */
    @Accessor(propertyName = "catalogVersion", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setCatalogVersion(CatalogVersion catalogVersion) {
        this.catalogVersion = catalogVersion;
    }
}
