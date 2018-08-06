/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.cms.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.infrastructure.annotation.Relation;

import io.spotnext.itemtype.cms.model.AbstractCmsComponent;
import io.spotnext.itemtype.cms.model.ContentSlot;

import java.io.Serializable;

import java.lang.String;

import java.util.Set;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "cmscomponent")
public class CmsComponent extends AbstractCmsComponent {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "cmscomponent";
    public static final String PROPERTY_CONTENT_SLOT = "contentSlot";
    @Property(readable = true, writable = true)
    @Relation(collectionType = io.spotnext.core.infrastructure.type.RelationCollectionType.Set, relationName = "ContentSlot2CmsComponent", mappedTo = "components", type = io.spotnext.core.infrastructure.type.RelationType.ManyToMany, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.TARGET)
    public Set<ContentSlot> contentSlot;

    @Accessor(propertyName = "contentSlot", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setContentSlot(Set<ContentSlot> contentSlot) {
        this.contentSlot = contentSlot;
    }

    @Accessor(propertyName = "contentSlot", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<ContentSlot> getContentSlot() {
        return this.contentSlot;
    }
}
