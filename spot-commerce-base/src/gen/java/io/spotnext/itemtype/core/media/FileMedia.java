/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.core.media;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;

import io.spotnext.itemtype.core.media.Media;

import java.io.Serializable;

import java.lang.Byte;
import java.lang.String;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "filemedia")
public class FileMedia extends Media {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "filemedia";
    public static final String PROPERTY_DATA_PATH = "dataPath";
    public static final String PROPERTY_DATA = "data";

    /**
     * The file path to the data object.
     */
    @Property(readable = true, writable = true)
    protected String dataPath;

    /**
     * The content of the data object (read from the filesystem).
     */
    @Property(readable = true, itemValueProvider = "mediaDataValueProvider", writable = true)
    protected Byte[] data;

    /**
     * The content of the data object (read from the filesystem).
     */
    @Accessor(propertyName = "data", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setData(Byte[] data) {
        this.data = data;
    }

    /**
     * The content of the data object (read from the filesystem).
     */
    @Accessor(propertyName = "data", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Byte[] getData() {
        return this.data;
    }

    /**
     * The file path to the data object.
     */
    @Accessor(propertyName = "dataPath", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getDataPath() {
        return this.dataPath;
    }

    /**
     * The file path to the data object.
     */
    @Accessor(propertyName = "dataPath", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }
}
