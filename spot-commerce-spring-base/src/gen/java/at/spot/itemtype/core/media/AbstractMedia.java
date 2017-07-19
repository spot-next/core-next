/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.core.media;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.itemtype.core.UniqueIdItem;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@ItemType(typeCode = "abstractmedia")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class AbstractMedia extends UniqueIdItem {
    private static final long serialVersionUID = -1L;

    /** The encoding of the content, eg. "UTF-8" or "base64". */
    @Property
    protected String encoding;

    /** The mime type of the content. */
    @Property
    protected String mimeType;

    @GetProperty
    public String getEncoding() {
        return this.encoding;
    }

    @GetProperty
    public String getMimeType() {
        return this.mimeType;
    }

    @SetProperty
    public void setEncoding(String encoding) {
        this.encoding = encoding;
        markAsDirty("encoding");
    }

    @SetProperty
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
        markAsDirty("mimeType");
    }
}
