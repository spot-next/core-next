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

import java.io.Serializable;

import java.lang.String;

import java.util.Set;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "abstractcmscontainercomponent")
public abstract class AbstractCmsContainerComponent extends AbstractCmsComponent {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "abstractcmscontainercomponent";
    public static final String PROPERTY_COMPONENTS = "components";
    @Relation(collectionType = io.spotnext.core.infrastructure.type.RelationCollectionType.Set, relationName = "AbstractCmsContainerComponent2AbstractCmsComponent", mappedTo = "container", type = io.spotnext.core.infrastructure.type.RelationType.ManyToMany, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.SOURCE)
    @Property(readable = true, writable = true)
    public Set<AbstractCmsComponent> components;

    @Accessor(propertyName = "components", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<AbstractCmsComponent> getComponents() {
        return this.components;
    }

    @Accessor(propertyName = "components", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setComponents(Set<AbstractCmsComponent> components) {
        this.components = components;
    }
}
