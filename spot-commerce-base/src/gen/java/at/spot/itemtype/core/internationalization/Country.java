/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.core.internationalization;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.core.model.Item;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;


@ItemType(typeCode = "country")
@Entity
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Country extends Item {
    private static final long serialVersionUID = -1L;

    /**
    * The ISO-3 code of the country.
    *
    * @see https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3.
     */
    @Property
    @NotNull
    @Length(min = 3, max = 3)
    protected String iso3Code;

    /**
    * The ISO-2 code of the country.
    *
    * @see https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2.
     */
    @Property(unique = true)
    @NotNull
    @Length(min = 2, max = 2)
    protected String isoCode;

    /** The languages available for that country. */
    @Property
    @ElementCollection
    protected List<Language> languages;

    /** The long name of the country, eg. "Republic of Austria". */
    @Property
    protected String longName;

    /** The short name of the country, eg. "Austria"". */
    @Property
    protected String shortName;

    @GetProperty
    public String getIso3Code() {
        return this.iso3Code;
    }

    @GetProperty
    public String getIsoCode() {
        return this.isoCode;
    }

    @GetProperty
    public List<Language> getLanguages() {
        return this.languages;
    }

    @GetProperty
    public String getLongName() {
        return this.longName;
    }

    @GetProperty
    public String getShortName() {
        return this.shortName;
    }

    @SetProperty
    public void setIso3Code(String iso3Code) {
        this.iso3Code = iso3Code;
        markAsDirty("iso3Code");
    }

    @SetProperty
    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
        markAsDirty("isoCode");
    }

    @SetProperty
    public void setLanguages(List<Language> languages) {
        this.languages = languages;
        markAsDirty("languages");
    }

    @SetProperty
    public void setLongName(String longName) {
        this.longName = longName;
        markAsDirty("longName");
    }

    @SetProperty
    public void setShortName(String shortName) {
        this.shortName = shortName;
        markAsDirty("shortName");
    }
}
