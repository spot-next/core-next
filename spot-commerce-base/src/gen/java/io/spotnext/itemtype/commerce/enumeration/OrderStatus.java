/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.commerce.enumeration;


/**
 * The render engine used to render cms items.
 */
@SuppressWarnings("unchecked")
public enum OrderStatus {OPEN("OPEN"),
    APPROVAL_PENDING("APPROVAL_PENDING"),
    APPROVED("APPROVED"),
    APPROVAL_REJECTED("APPROVAL_REJECTED"),
    CANCELLED("CANCELLED"),
    UNKNOWN("UNKNOWN");

    private String internalValue;

    private OrderStatus(String internalValue) {
        this.internalValue = internalValue;
    }

    /**
     * Returns the internal value of the current enum.
     */
    public String getInternalName() {
        return this.internalValue;
    }
}
