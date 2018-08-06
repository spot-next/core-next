/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.core.internationalization;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;

import io.spotnext.itemtype.core.UniqueIdItem;

import java.io.Serializable;

import java.lang.String;

import java.util.Locale;

import javax.validation.constraints.NotNull;


/**
 * This type can be used to store localized values different languages/locales.
 */
@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "localizationvalue")
public class LocalizationValue extends UniqueIdItem {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "localizationvalue";
    public static final String PROPERTY_LOCALE = "locale";
    public static final String PROPERTY_VALUE = "value";

    /**
     * The locale of the translation key.
     */
    @Property(readable = true, unique = true, writable = true)
    @NotNull
    protected Locale locale;

    /**
     * The localized value of the key.
     */
    @Property(readable = true, writable = true)
    protected String value;

    /**
     * The locale of the translation key.
     */
    @Accessor(propertyName = "locale", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Locale getLocale() {
        return this.locale;
    }

    /**
     * The localized value of the key.
     */
    @Accessor(propertyName = "value", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getValue() {
        return this.value;
    }

    /**
     * The locale of the translation key.
     */
    @Accessor(propertyName = "locale", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * The localized value of the key.
     */
    @Accessor(propertyName = "value", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setValue(String value) {
        this.value = value;
    }
}
