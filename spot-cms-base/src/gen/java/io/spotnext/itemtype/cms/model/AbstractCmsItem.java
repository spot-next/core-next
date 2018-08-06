/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.cms.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.infrastructure.annotation.Relation;
import io.spotnext.core.infrastructure.support.ItemCollectionFactory;

import io.spotnext.itemtype.cms.model.CmsRestriction;
import io.spotnext.itemtype.core.UniqueIdItem;
import io.spotnext.itemtype.core.catalog.Catalog;

import java.io.Serializable;

import java.lang.Boolean;
import java.lang.String;

import java.util.Set;

import javax.validation.constraints.NotNull;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "abstractcmsitem")
public abstract class AbstractCmsItem extends UniqueIdItem {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "abstractcmsitem";
    public static final String PROPERTY_CATALOG = "catalog";
    public static final String PROPERTY_ONLY_ONE_RESTRICTION_MUST_APPLY = "onlyOneRestrictionMustApply";
    public static final String PROPERTY_CMS_RESTRICTIONS = "cmsRestrictions";

    /**
     * The content catalog of the item.
     */
    @NotNull
    @Property(readable = true, unique = true, writable = true)
    protected Catalog catalog;

    /**
     * f set to true, only one restriction must evaluate to "show cms item" <br>                                        for the item to be visible.
     */
    @Property(readable = true, writable = true)
    protected Boolean onlyOneRestrictionMustApply;
    @Relation(collectionType = io.spotnext.core.infrastructure.type.RelationCollectionType.Set, relationName = "AbstractCmsItem2CmsRestriction", mappedTo = "cmsItem", type = io.spotnext.core.infrastructure.type.RelationType.OneToMany, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.SOURCE)
    @Property(readable = true, writable = true)
    public Set<CmsRestriction> cmsRestrictions;

    /**
     * The content catalog of the item.
     */
    @Accessor(propertyName = "catalog", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Catalog getCatalog() {
        return this.catalog;
    }

    /**
     * The content catalog of the item.
     */
    @Accessor(propertyName = "catalog", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    @Accessor(propertyName = "cmsRestrictions", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<CmsRestriction> getCmsRestrictions() {
        return ItemCollectionFactory.wrap(this, "cmsRestrictions",
            this.cmsRestrictions);
    }

    /**
     * f set to true, only one restriction must evaluate to "show cms item" <br>                                        for the item to be visible.
     */
    @Accessor(propertyName = "onlyOneRestrictionMustApply", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Boolean getOnlyOneRestrictionMustApply() {
        return this.onlyOneRestrictionMustApply;
    }

    /**
     * f set to true, only one restriction must evaluate to "show cms item" <br>                                        for the item to be visible.
     */
    @Accessor(propertyName = "onlyOneRestrictionMustApply", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setOnlyOneRestrictionMustApply(
        Boolean onlyOneRestrictionMustApply) {
        this.onlyOneRestrictionMustApply = onlyOneRestrictionMustApply;
    }

    @Accessor(propertyName = "cmsRestrictions", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setCmsRestrictions(Set<CmsRestriction> cmsRestrictions) {
        this.cmsRestrictions = cmsRestrictions;
    }
}
