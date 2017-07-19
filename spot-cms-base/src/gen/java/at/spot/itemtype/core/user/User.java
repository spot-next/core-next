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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;


/**
* Represents a user.
 */
@ItemType(typeCode = "user")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class User extends Principal {
    private static final long serialVersionUID = -1L;

    /** The user's addresses. */
    @Property
    @Relation(type = RelationType.OneToMany, mappedTo = "owner", referencedType = Address.class)
    protected List<Address> addresses;

    /** The main email address of the user. */
    @Property
    protected String emailAddress;

    /** The login password, can be encrypted. */
    @Property
    protected String password;

    @GetProperty
    public List<Address> getAddresses() {
        return this.addresses;
    }

    @GetProperty
    public String getEmailAddress() {
        return this.emailAddress;
    }

    @GetProperty
    public String getPassword() {
        return this.password;
    }

    @SetProperty
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
        markAsDirty("addresses");
    }

    @SetProperty
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        markAsDirty("emailAddress");
    }

    @SetProperty
    public void setPassword(String password) {
        this.password = password;
        markAsDirty("password");
    }
}
