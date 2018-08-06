/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.cms.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;

import io.spotnext.itemtype.cms.model.CmsPageTemplate;

import java.io.Serializable;

import java.lang.String;

import java.util.Set;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "cmspage")
public class CmsPage extends CmsPageTemplate {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "cmspage";
    public static final String PROPERTY_URL_MATCHES = "urlMatches";
    public static final String PROPERTY_TEMPLATE = "template";

    /**
     * Holds the regex string to determine which URL should<br>                                                be handler by this page.
     */
    @Property(readable = true, writable = true)
    protected Set<String> urlMatches;
    @Property(readable = true, writable = true)
    protected CmsPageTemplate template;

    @Accessor(propertyName = "template", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setTemplate(CmsPageTemplate template) {
        this.template = template;
    }

    @Accessor(propertyName = "template", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public CmsPageTemplate getTemplate() {
        return this.template;
    }

    /**
     * Holds the regex string to determine which URL should<br>                                                be handler by this page.
     */
    @Accessor(propertyName = "urlMatches", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<String> getUrlMatches() {
        return this.urlMatches;
    }

    /**
     * Holds the regex string to determine which URL should<br>                                                be handler by this page.
     */
    @Accessor(propertyName = "urlMatches", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setUrlMatches(Set<String> urlMatches) {
        this.urlMatches = urlMatches;
    }
}
