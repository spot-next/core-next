/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.commerce.customer;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.itemtype.core.user.User;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Date;


@ItemType(typeCode = "customer")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Customer extends User {
    private static final long serialVersionUID = -1L;
    @Property
    protected Date birthDay;
    @Property
    protected String firstName;
    @Property
    protected String lastName;

    @GetProperty
    public Date getBirthDay() {
        return this.birthDay;
    }

    @GetProperty
    public String getFirstName() {
        return this.firstName;
    }

    @GetProperty
    public String getLastName() {
        return this.lastName;
    }

    @SetProperty
    public void setBirthDay(Date birthDay) {
        this.birthDay = birthDay;
        markAsDirty("birthDay");
    }

    @SetProperty
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        markAsDirty("firstName");
    }

    @SetProperty
    public void setLastName(String lastName) {
        this.lastName = lastName;
        markAsDirty("lastName");
    }
}
