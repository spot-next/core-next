/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.commerce.store;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;

import io.spotnext.itemtype.core.UniqueIdItem;
import io.spotnext.itemtype.core.internationalization.Country;
import io.spotnext.itemtype.core.internationalization.LocalizedString;

import java.io.Serializable;

import java.lang.String;

import java.math.BigDecimal;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "tax")
public class Tax extends UniqueIdItem {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "tax";
    public static final String PROPERTY_VALUE = "value";
    public static final String PROPERTY_DESCRIPTION = "description";
    public static final String PROPERTY_COUNTRY = "country";
    @Property(readable = true, writable = true)
    protected BigDecimal value;
    @Property(readable = true, writable = true)
    protected LocalizedString description;
    @Property(readable = true, writable = true)
    protected Country country;

    @Accessor(propertyName = "country", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setCountry(Country country) {
        this.country = country;
    }

    @Accessor(propertyName = "value", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    @Accessor(propertyName = "value", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public BigDecimal getValue() {
        return this.value;
    }

    @Accessor(propertyName = "description", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setDescription(LocalizedString description) {
        this.description = description;
    }

    @Accessor(propertyName = "country", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Country getCountry() {
        return this.country;
    }

    @Accessor(propertyName = "description", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public LocalizedString getDescription() {
        return this.description;
    }
}
