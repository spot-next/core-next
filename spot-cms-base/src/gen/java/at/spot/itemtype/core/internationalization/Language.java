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

import java.util.Locale;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;


/**
* This represents a language object and it's locale.
 */
@ItemType(typeCode = "language")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Language extends Item {
    private static final long serialVersionUID = -1L;

    /**
    * The ISO-3 code of the language.
    *
    * @see https://en.wikipedia.org/wiki/ISO_639-3.
     */
    @Property(unique = true)
    @NotNull
    @Length(min = 3, max = 3)
    protected String iso3Code;

    /** The java locale associated with this language. */
    @Property
    protected Locale locale;

    @GetProperty
    public String getIso3Code() {
        return this.iso3Code;
    }

    @GetProperty
    public Locale getLocale() {
        return this.locale;
    }

    @SetProperty
    public void setIso3Code(String iso3Code) {
        this.iso3Code = iso3Code;
        markAsDirty("iso3Code");
    }

    @SetProperty
    public void setLocale(Locale locale) {
        this.locale = locale;
        markAsDirty("locale");
    }
}
