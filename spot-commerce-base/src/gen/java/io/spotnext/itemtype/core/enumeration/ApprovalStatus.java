/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.core.enumeration;


/**
 * The approval status, eg. of products or CMS items.
 */
@SuppressWarnings("unchecked")
public enum ApprovalStatus {APPROVED("APPROVED"),
    UNAPPROVED("UNAPPROVED");

    private String internalValue;

    private ApprovalStatus(String internalValue) {
        this.internalValue = internalValue;
    }

    /**
     * Returns the internal value of the current enum.
     */
    public String getInternalName() {
        return this.internalValue;
    }
}
