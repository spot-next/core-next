/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.cms.enumeration;


/**
 * Defines the orientation, eg. for layouts.
 */
@SuppressWarnings("unchecked")
public enum Orientation {TOP("TOP"),
    BOTTOM("BOTTOM"),
    LEFT("LEFT"),
    RIGHT("RIGHT");

    private String internalValue;

    private Orientation(String internalValue) {
        this.internalValue = internalValue;
    }

    /**
     * Returns the internal value of the current enum.
     */
    public String getInternalName() {
        return this.internalValue;
    }
}
