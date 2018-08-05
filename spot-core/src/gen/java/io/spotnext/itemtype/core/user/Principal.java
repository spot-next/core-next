/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.core.user;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.infrastructure.annotation.Relation;

import io.spotnext.itemtype.core.UniqueIdItem;
import io.spotnext.itemtype.core.user.PrincipalGroup;

import java.io.Serializable;

import java.lang.String;

import java.util.Set;


/**
 * The base type all user related item types.
 */
@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "principal")
public abstract class Principal extends UniqueIdItem {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "principal";
    public static final String PROPERTY_SHORT_NAME = "shortName";
    public static final String PROPERTY_GROUPS = "groups";

    /**
     * The short name identifying the principal object.
     */
    @Property(readable = true, writable = true)
    protected String shortName;

    /**
     * The relation between principal groups and principals
     */
    @Relation(collectionType = io.spotnext.core.infrastructure.type.RelationCollectionType.List, relationName = "PrincipalGroup2Principal", mappedTo = "members", type = io.spotnext.core.infrastructure.type.RelationType.ManyToMany, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.TARGET)
    @Property(readable = true, writable = true)
    public Set<PrincipalGroup> groups;

    /**
     * The short name identifying the principal object.
     */
    @Accessor(propertyName = "shortName", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * The short name identifying the principal object.
     */
    @Accessor(propertyName = "shortName", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getShortName() {
        return this.shortName;
    }

    /**
     * The relation between principal groups and principals
     */
    @Accessor(propertyName = "groups", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setGroups(Set<PrincipalGroup> groups) {
        this.groups = groups;
    }

    /**
     * The relation between principal groups and principals
     */
    @Accessor(propertyName = "groups", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<PrincipalGroup> getGroups() {
        return this.groups;
    }
}
