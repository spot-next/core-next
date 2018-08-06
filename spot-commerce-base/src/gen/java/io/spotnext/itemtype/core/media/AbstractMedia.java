/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.core.media;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;

import io.spotnext.itemtype.core.CatalogItem;

import java.io.Serializable;

import java.lang.String;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "abstractmedia")
public abstract class AbstractMedia extends CatalogItem {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "abstractmedia";
    public static final String PROPERTY_MIME_TYPE = "mimeType";
    public static final String PROPERTY_ENCODING = "encoding";

    /**
     * The mime type of the content.
     */
    @Property(readable = true, writable = true)
    protected String mimeType;

    /**
     * The encoding of the content, eg. "UTF-8" or "base64".
     */
    @Property(readable = true, writable = true)
    protected String encoding;

    /**
     * The mime type of the content.
     */
    @Accessor(propertyName = "mimeType", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * The encoding of the content, eg. "UTF-8" or "base64".
     */
    @Accessor(propertyName = "encoding", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * The mime type of the content.
     */
    @Accessor(propertyName = "mimeType", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getMimeType() {
        return this.mimeType;
    }

    /**
     * The encoding of the content, eg. "UTF-8" or "base64".
     */
    @Accessor(propertyName = "encoding", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getEncoding() {
        return this.encoding;
    }
}
