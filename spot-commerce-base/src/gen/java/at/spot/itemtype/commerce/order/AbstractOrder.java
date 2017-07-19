/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.commerce.order;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.itemtype.commerce.customer.Customer;

import at.spot.itemtype.core.UniqueIdItem;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

import javax.validation.constraints.NotNull;


/**
* The abstract base type for orders and carts.
 */
@ItemType(typeCode = "abstractorder")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class AbstractOrder extends UniqueIdItem {
    private static final long serialVersionUID = -1L;
    @Property(unique = true)
    @NotNull
    protected Customer customer;
    @Property
    protected List<AbstractOrderEntry> entries;

    @GetProperty
    public Customer getCustomer() {
        return this.customer;
    }

    @GetProperty
    public List<AbstractOrderEntry> getEntries() {
        return this.entries;
    }

    @SetProperty
    public void setCustomer(Customer customer) {
        this.customer = customer;
        markAsDirty("customer");
    }

    @SetProperty
    public void setEntries(List<AbstractOrderEntry> entries) {
        this.entries = entries;
        markAsDirty("entries");
    }
}
