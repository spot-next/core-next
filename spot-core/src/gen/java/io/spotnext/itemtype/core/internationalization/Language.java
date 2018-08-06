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

import io.spotnext.itemtype.core.internationalization.Country;
import io.spotnext.itemtype.core.internationalization.LocalizedString;

import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

import java.lang.String;

import java.util.Locale;
import java.util.Set;

import javax.validation.constraints.NotNull;


/**
 * This represents a language object and it's locale.
 */
@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "language")
public class Language extends Item {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "language";
    public static final String PROPERTY_ISO3_CODE = "iso3Code";
    public static final String PROPERTY_ISO_CODE = "isoCode";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_COUNTRIES = "countries";

    /**
     * The ISO-3 code of the language.<br>                    @see https://en.wikipedia.org/wiki/ISO_639-3.
     */
    @NotNull
    @Property(readable = true, unique = true, writable = true)
    @Length(min = 3, max = 3)
    protected String iso3Code;

    /**
     * The ISO-3 code of the language.<br>                    @see https://en.wikipedia.org/wiki/ISO_639-1.
     */
    @Length(max = 2)
    @Property(readable = true, writable = true)
    protected String isoCode;

    /**
     * The international name of the language.
     */
    @Property(readable = true, writable = true)
    protected LocalizedString name = new LocalizedString();

    /**
     * The languages available for that country.
     */
    @Property(readable = true, writable = true)
    @Relation(collectionType = io.spotnext.core.infrastructure.type.RelationCollectionType.Set, relationName = "Country2Language", mappedTo = "languages", type = io.spotnext.core.infrastructure.type.RelationType.ManyToMany, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.TARGET)
    public Set<Country> countries;

    /**
     * The international name of the language.
     */
    @Accessor(propertyName = "name", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setName(String name) {
        this.name.set(name);
    }

    /**
     * The languages available for that country.
     */
    @Accessor(propertyName = "countries", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setCountries(Set<Country> countries) {
        this.countries = countries;
    }

    /**
     * The ISO-3 code of the language.<br>                    @see https://en.wikipedia.org/wiki/ISO_639-3.
     */
    @Accessor(propertyName = "iso3Code", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getIso3Code() {
        return this.iso3Code;
    }

    /**
     * The international name of the language.
     */
    @Accessor(propertyName = "name", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setName(String name, Locale locale) {
        this.name.set(locale, name);
    }

    /**
     * The ISO-3 code of the language.<br>                    @see https://en.wikipedia.org/wiki/ISO_639-3.
     */
    @Accessor(propertyName = "iso3Code", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setIso3Code(String iso3Code) {
        this.iso3Code = iso3Code;
    }

    /**
     * The ISO-3 code of the language.<br>                    @see https://en.wikipedia.org/wiki/ISO_639-1.
     */
    @Accessor(propertyName = "isoCode", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    /**
     * The international name of the language.
     */
    @Accessor(propertyName = "name", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getName() {
        return this.name.get();
    }

    /**
     * The languages available for that country.
     */
    @Accessor(propertyName = "countries", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<Country> getCountries() {
        return this.countries;
    }

    /**
     * The ISO-3 code of the language.<br>                    @see https://en.wikipedia.org/wiki/ISO_639-1.
     */
    @Accessor(propertyName = "isoCode", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getIsoCode() {
        return this.isoCode;
    }

    /**
     * The international name of the language.
     */
    @Accessor(propertyName = "name", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getName(Locale locale) {
        return this.name.get(locale);
    }
}
