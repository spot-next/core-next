/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.commerce.store;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.core.infrastructure.type.LocalizedString;

import at.spot.itemtype.core.UniqueIdItem;

import at.spot.itemtype.core.internationalization.Country;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.math.BigDecimal;

import javax.persistence.Entity;


@ItemType(typeCode = "tax")
@Entity
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Tax extends UniqueIdItem {
    private static final long serialVersionUID = -1L;
    @Property
    protected Country country;
    @Property
    protected LocalizedString description;
    @Property
    protected BigDecimal value;

    @GetProperty
    public Country getCountry() {
        return this.country;
    }

    @GetProperty
    public LocalizedString getDescription() {
        return this.description;
    }

    @GetProperty
    public BigDecimal getValue() {
        return this.value;
    }

    @SetProperty
    public void setCountry(Country country) {
        this.country = country;
        markAsDirty("country");
    }

    @SetProperty
    public void setDescription(LocalizedString description) {
        this.description = description;
        markAsDirty("description");
    }

    @SetProperty
    public void setValue(BigDecimal value) {
        this.value = value;
        markAsDirty("value");
    }
}
