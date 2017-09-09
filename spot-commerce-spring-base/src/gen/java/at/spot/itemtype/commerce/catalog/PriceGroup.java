/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.commerce.catalog;

import at.spot.core.infrastructure.annotation.ItemType;

import at.spot.itemtype.core.UniqueIdItem;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.persistence.Entity;


/**
* This is the container for all categories and products.
 */
@ItemType(typeCode = "pricegroup")
@Entity
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class PriceGroup extends UniqueIdItem {
    private static final long serialVersionUID = -1L;
}
