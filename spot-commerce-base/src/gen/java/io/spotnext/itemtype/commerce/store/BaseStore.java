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

import java.io.Serializable;

import java.lang.String;

import java.util.Set;


/**
 * This holds the base configuration for a store.
 */
@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "basestore")
public class BaseStore extends UniqueIdItem {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "basestore";
    public static final String PROPERTY_NAME = "name";
    public static final String PROPERTY_URL_MATCHERS = "urlMatchers";
    public static final String PROPERTY_COUNTRIES = "countries";

    /**
     * The name of the base store.
     */
    @Property(readable = true, writable = true)
    protected String name;

    /**
     * These regex strings are used to match browser url to the base store.
     */
    @Property(readable = true, writable = true)
    protected Set<String> urlMatchers;

    /**
     * The countries that are assigned to this base store.
     */
    @Property(readable = true, writable = true)
    protected Set<Country> countries;

    /**
     * The countries that are assigned to this base store.
     */
    @Accessor(propertyName = "countries", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<Country> getCountries() {
        return this.countries;
    }

    /**
     * These regex strings are used to match browser url to the base store.
     */
    @Accessor(propertyName = "urlMatchers", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<String> getUrlMatchers() {
        return this.urlMatchers;
    }

    /**
     * The name of the base store.
     */
    @Accessor(propertyName = "name", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getName() {
        return this.name;
    }

    /**
     * The countries that are assigned to this base store.
     */
    @Accessor(propertyName = "countries", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setCountries(Set<Country> countries) {
        this.countries = countries;
    }

    /**
     * The name of the base store.
     */
    @Accessor(propertyName = "name", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setName(String name) {
        this.name = name;
    }

    /**
     * These regex strings are used to match browser url to the base store.
     */
    @Accessor(propertyName = "urlMatchers", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setUrlMatchers(Set<String> urlMatchers) {
        this.urlMatchers = urlMatchers;
    }
}
