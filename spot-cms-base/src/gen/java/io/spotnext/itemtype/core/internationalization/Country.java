/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.core.internationalization;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.infrastructure.annotation.Relation;
import io.spotnext.core.types.Item;

import io.spotnext.itemtype.core.internationalization.Language;
import io.spotnext.itemtype.core.internationalization.LocalizedString;

import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

import java.lang.String;

import java.util.Locale;
import java.util.Set;

import javax.validation.constraints.NotNull;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "country")
public class Country extends Item {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "country";
    public static final String PROPERTY_ISO_CODE = "isoCode";
    public static final String PROPERTY_ISO3_CODE = "iso3Code";
    public static final String PROPERTY_SHORT_NAME = "shortName";
    public static final String PROPERTY_LONG_NAME = "longName";
    public static final String PROPERTY_PHONE_COUNTRY_CODE = "phoneCountryCode";
    public static final String PROPERTY_LANGUAGES = "languages";

    /**
     * The ISO-2 code of the country.<br>                    @see https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2.
     */
    @Length(min = 2, max = 2)
    @Property(readable = true, unique = true, writable = true)
    @NotNull
    protected String isoCode;

    /**
     * The ISO-3 code of the country.<br>                                @see https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3.
     */
    @NotNull
    @Property(readable = true, writable = true)
    @Length(min = 3, max = 3)
    protected String iso3Code;

    /**
     * The short name of the country, eg. "Austria".
     */
    @Property(readable = true, writable = true)
    protected LocalizedString shortName = new LocalizedString();

    /**
     * The long name of the country, eg. "Republic of Austria".
     */
    @Property(readable = true, writable = true)
    protected LocalizedString longName = new LocalizedString();

    /**
     * The phone country code, eg. +43 or 0043 for Austria
     */
    @Property(readable = true, writable = true)
    protected String phoneCountryCode;

    /**
     * The languages available for that country.
     */
    @Property(readable = true, writable = true)
    @Relation(collectionType = io.spotnext.core.infrastructure.type.RelationCollectionType.Set, relationName = "Country2Language", mappedTo = "countries", type = io.spotnext.core.infrastructure.type.RelationType.ManyToMany, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.SOURCE)
    public Set<Language> languages;

    /**
     * The phone country code, eg. +43 or 0043 for Austria
     */
    @Accessor(propertyName = "phoneCountryCode", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setPhoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
    }

    /**
     * The short name of the country, eg. "Austria".
     */
    @Accessor(propertyName = "shortName", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getShortName(Locale locale) {
        return this.shortName.get(locale);
    }

    /**
     * The short name of the country, eg. "Austria".
     */
    @Accessor(propertyName = "shortName", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getShortName() {
        return this.shortName.get();
    }

    /**
     * The ISO-2 code of the country.<br>                    @see https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2.
     */
    @Accessor(propertyName = "isoCode", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getIsoCode() {
        return this.isoCode;
    }

    /**
     * The languages available for that country.
     */
    @Accessor(propertyName = "languages", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }

    /**
     * The ISO-3 code of the country.<br>                                @see https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3.
     */
    @Accessor(propertyName = "iso3Code", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setIso3Code(String iso3Code) {
        this.iso3Code = iso3Code;
    }

    /**
     * The short name of the country, eg. "Austria".
     */
    @Accessor(propertyName = "shortName", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setShortName(String shortName, Locale locale) {
        this.shortName.set(locale, shortName);
    }

    /**
     * The short name of the country, eg. "Austria".
     */
    @Accessor(propertyName = "shortName", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setShortName(String shortName) {
        this.shortName.set(shortName);
    }

    /**
     * The long name of the country, eg. "Republic of Austria".
     */
    @Accessor(propertyName = "longName", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getLongName() {
        return this.longName.get();
    }

    /**
     * The long name of the country, eg. "Republic of Austria".
     */
    @Accessor(propertyName = "longName", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getLongName(Locale locale) {
        return this.longName.get(locale);
    }

    /**
     * The ISO-3 code of the country.<br>                                @see https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3.
     */
    @Accessor(propertyName = "iso3Code", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getIso3Code() {
        return this.iso3Code;
    }

    /**
     * The ISO-2 code of the country.<br>                    @see https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2.
     */
    @Accessor(propertyName = "isoCode", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    /**
     * The phone country code, eg. +43 or 0043 for Austria
     */
    @Accessor(propertyName = "phoneCountryCode", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getPhoneCountryCode() {
        return this.phoneCountryCode;
    }

    /**
     * The long name of the country, eg. "Republic of Austria".
     */
    @Accessor(propertyName = "longName", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setLongName(String longName, Locale locale) {
        this.longName.set(locale, longName);
    }

    /**
     * The long name of the country, eg. "Republic of Austria".
     */
    @Accessor(propertyName = "longName", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setLongName(String longName) {
        this.longName.set(longName);
    }

    /**
     * The languages available for that country.
     */
    @Accessor(propertyName = "languages", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<Language> getLanguages() {
        return this.languages;
    }
}
