/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.cms.enumeration;


/**
 * Represents an HTML link target used.
 */
@SuppressWarnings("unchecked")
public enum HtmlLinkTarget {BLANK("BLANK"),
    SELF("SELF"),
    PARENT("PARENT"),
    TOP("TOP");

    private String internalValue;

    private HtmlLinkTarget(String internalValue) {
        this.internalValue = internalValue;
    }

    /**
     * Returns the internal value of the current enum.
     */
    public String getInternalName() {
        return this.internalValue;
    }
}
