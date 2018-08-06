/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.core.user;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.infrastructure.annotation.Relation;

import io.spotnext.itemtype.core.user.Address;
import io.spotnext.itemtype.core.user.User;

import java.io.Serializable;

import java.lang.String;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "useraddress")
public class UserAddress extends Address {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "useraddress";
    public static final String PROPERTY_EMAIL_ADDRESS = "emailAddress";
    public static final String PROPERTY_PHONE = "phone";
    public static final String PROPERTY_OWNER = "owner";
    @Property(readable = true, writable = true)
    protected String emailAddress;
    @Property(readable = true, writable = true)
    protected String phone;

    /**
     * Defines a address ownership relation.
     */
    @Property(readable = true, writable = true)
    @Relation(relationName = "User2Address", mappedTo = "addresses", type = io.spotnext.core.infrastructure.type.RelationType.ManyToOne, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.TARGET)
    public User owner;

    @Accessor(propertyName = "phone", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getPhone() {
        return this.phone;
    }

    @Accessor(propertyName = "phone", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Accessor(propertyName = "emailAddress", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getEmailAddress() {
        return this.emailAddress;
    }

    @Accessor(propertyName = "emailAddress", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Defines a address ownership relation.
     */
    @Accessor(propertyName = "owner", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public User getOwner() {
        return this.owner;
    }

    /**
     * Defines a address ownership relation.
     */
    @Accessor(propertyName = "owner", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setOwner(User owner) {
        this.owner = owner;
    }
}
