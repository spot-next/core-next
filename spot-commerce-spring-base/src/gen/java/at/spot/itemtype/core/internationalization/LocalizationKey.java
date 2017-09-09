/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.core.internationalization;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.itemtype.core.UniqueIdItem;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Locale;

import javax.persistence.Entity;

import javax.validation.constraints.NotNull;


/**
* This type can be used to store localized values different languages/locales.
 */
@ItemType(typeCode = "localizationkey")
@Entity
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class LocalizationKey extends UniqueIdItem {
    private static final long serialVersionUID = -1L;

    /** The locale of the translation key. */
    @Property(unique = true)
    @NotNull
    protected Locale locale;

    /** The localized value of the key. */
    @Property
    protected String value;

    @GetProperty
    public Locale getLocale() {
        return this.locale;
    }

    @GetProperty
    public String getValue() {
        return this.value;
    }

    @SetProperty
    public void setLocale(Locale locale) {
        this.locale = locale;
        markAsDirty("locale");
    }

    @SetProperty
    public void setValue(String value) {
        this.value = value;
        markAsDirty("value");
    }
}
