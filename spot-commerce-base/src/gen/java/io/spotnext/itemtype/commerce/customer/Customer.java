/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.commerce.customer;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;

import io.spotnext.itemtype.core.user.User;

import java.io.Serializable;

import java.lang.String;

import java.util.Date;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "customer")
public class Customer extends User {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "customer";
    public static final String PROPERTY_FIRST_NAME = "firstName";
    public static final String PROPERTY_LAST_NAME = "lastName";
    public static final String PROPERTY_BIRTH_DAY = "birthDay";
    @Property(readable = true, writable = true)
    protected String firstName;
    @Property(readable = true, writable = true)
    protected String lastName;
    @Property(readable = true, writable = true)
    protected Date birthDay;

    @Accessor(propertyName = "firstName", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getFirstName() {
        return this.firstName;
    }

    @Accessor(propertyName = "lastName", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Accessor(propertyName = "lastName", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getLastName() {
        return this.lastName;
    }

    @Accessor(propertyName = "firstName", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Accessor(propertyName = "birthDay", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
    }

    @Accessor(propertyName = "birthDay", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Date getBirthDay() {
        return this.birthDay;
    }
}
