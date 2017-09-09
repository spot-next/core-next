/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.commerce.store;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.itemtype.core.UniqueIdItem;

import at.spot.itemtype.core.internationalization.Country;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;


/**
* This holds the base configuration for a store.
 */
@ItemType(typeCode = "basestore")
@Entity
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class BaseStore extends UniqueIdItem {
    private static final long serialVersionUID = -1L;

    /** The countries that are assigned to this base store. */
    @Property
    @ElementCollection
    protected List<Country> countries;

    /** The name of the base store. */
    @Property
    protected String name;

    /** These regex strings are used to match browser url to the base store. */
    @Property
    @ElementCollection
    protected List<String> urlMatchers;

    @GetProperty
    public List<Country> getCountries() {
        return this.countries;
    }

    @GetProperty
    public String getName() {
        return this.name;
    }

    @GetProperty
    public List<String> getUrlMatchers() {
        return this.urlMatchers;
    }

    @SetProperty
    public void setCountries(List<Country> countries) {
        this.countries = countries;
        markAsDirty("countries");
    }

    @SetProperty
    public void setName(String name) {
        this.name = name;
        markAsDirty("name");
    }

    @SetProperty
    public void setUrlMatchers(List<String> urlMatchers) {
        this.urlMatchers = urlMatchers;
        markAsDirty("urlMatchers");
    }
}
