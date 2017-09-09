/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.core.internationalization;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.core.infrastructure.type.LocalizedString;

import at.spot.core.model.Item;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.persistence.Entity;

import javax.validation.constraints.NotNull;


/**
* Represents a currency.
 */
@ItemType(typeCode = "currency")
@Entity
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Currency extends Item {
    private static final long serialVersionUID = -1L;

    /** The iso code of the currency. */
    @Property(unique = true)
    @NotNull
    protected String isoCode;

    /** The localized name of the currency. */
    @Property
    protected LocalizedString name;

    @GetProperty
    public String getIsoCode() {
        return this.isoCode;
    }

    @GetProperty
    public LocalizedString getName() {
        return this.name;
    }

    @SetProperty
    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
        markAsDirty("isoCode");
    }

    @SetProperty
    public void setName(LocalizedString name) {
        this.name = name;
        markAsDirty("name");
    }
}
