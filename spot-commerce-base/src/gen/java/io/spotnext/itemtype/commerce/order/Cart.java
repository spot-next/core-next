/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.commerce.order;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.ItemType;

import io.spotnext.itemtype.commerce.order.AbstractOrder;

import java.io.Serializable;

import java.lang.String;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "cart")
public class Cart extends AbstractOrder {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "cart";
}
