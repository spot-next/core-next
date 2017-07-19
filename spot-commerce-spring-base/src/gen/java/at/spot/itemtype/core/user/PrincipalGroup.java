/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.core.user;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;


/**
* The base type all principal group-like structures.
 */
@ItemType(typeCode = "principalgroup")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class PrincipalGroup extends Principal {
    private static final long serialVersionUID = -1L;

    /** The members of the group. */
    @Property
    protected List<Principal> members;

    @GetProperty
    public List<Principal> getMembers() {
        return this.members;
    }

    @SetProperty
    public void setMembers(List<Principal> members) {
        this.members = members;
        markAsDirty("members");
    }
}
