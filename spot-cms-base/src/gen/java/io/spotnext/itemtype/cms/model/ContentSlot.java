/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.cms.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.infrastructure.annotation.Relation;

import io.spotnext.itemtype.cms.model.AbstractCmsItem;
import io.spotnext.itemtype.cms.model.CmsComponent;

import java.io.Serializable;

import java.lang.String;

import java.util.Set;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "contentslot")
public class ContentSlot extends AbstractCmsItem {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "contentslot";
    public static final String PROPERTY_COMPONENTS = "components";
    @Relation(collectionType = io.spotnext.core.infrastructure.type.RelationCollectionType.Set, relationName = "ContentSlot2CmsComponent", mappedTo = "contentSlot", type = io.spotnext.core.infrastructure.type.RelationType.ManyToMany, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.SOURCE)
    @Property(readable = true, writable = true)
    public Set<CmsComponent> components;

    @Accessor(propertyName = "components", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setComponents(Set<CmsComponent> components) {
        this.components = components;
    }

    @Accessor(propertyName = "components", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<CmsComponent> getComponents() {
        return this.components;
    }
}
