/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.commerce.order;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.core.model.Item;

import at.spot.itemtype.commerce.catalog.Product;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.validation.constraints.NotNull;


@ItemType(typeCode = "abstractorderentry")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class AbstractOrderEntry extends Item {
    private static final long serialVersionUID = -1L;
    @Property(unique = true)
    @NotNull
    protected AbstractOrder order;
    @Property(unique = true)
    @NotNull
    protected Product product;

    @GetProperty
    public AbstractOrder getOrder() {
        return this.order;
    }

    @GetProperty
    public Product getProduct() {
        return this.product;
    }

    @SetProperty
    public void setOrder(AbstractOrder order) {
        this.order = order;
        markAsDirty("order");
    }

    @SetProperty
    public void setProduct(Product product) {
        this.product = product;
        markAsDirty("product");
    }
}
