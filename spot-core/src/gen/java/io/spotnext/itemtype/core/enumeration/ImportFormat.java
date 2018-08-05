/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.core.enumeration;


/**
 * Formats that are supported for import of data.
 */
@SuppressWarnings("unchecked")
public enum ImportFormat {ImpEx("ImpEx"),
    JSON("JSON"),
    XML("XML");

    private String internalValue;

    private ImportFormat(String internalValue) {
        this.internalValue = internalValue;
    }

    /**
     * Returns the internal value of the current enum.
     */
    public String getInternalName() {
        return this.internalValue;
    }
}
