/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.core.user;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.core.infrastructure.type.RelationType;

import at.spot.itemtype.core.UniqueIdItem;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;


/**
* The base type all user related item types.
 */
@ItemType(typeCode = "principal")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class Principal extends UniqueIdItem {
    private static final long serialVersionUID = -1L;

    /** The principal groups assigned to the user. */
    @Property(isReference = true)
    @Relation(type = RelationType.ManyToMany, mappedTo = "members", referencedType = PrincipalGroup.class)
    protected List<PrincipalGroup> groups;

    /** The short name identifying the principal object. */
    @Property
    protected String shortName;

    @GetProperty
    public List<PrincipalGroup> getGroups() {
        return this.groups;
    }

    @GetProperty
    public String getShortName() {
        return this.shortName;
    }

    @SetProperty
    public void setGroups(List<PrincipalGroup> groups) {
        this.groups = groups;
        markAsDirty("groups");
    }

    @SetProperty
    public void setShortName(String shortName) {
        this.shortName = shortName;
        markAsDirty("shortName");
    }
}
