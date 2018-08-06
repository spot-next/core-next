/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.commerce.order;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.infrastructure.annotation.Relation;
import io.spotnext.core.types.Item;

import io.spotnext.itemtype.commerce.catalog.Product;
import io.spotnext.itemtype.commerce.order.AbstractOrder;

import java.io.Serializable;

import java.lang.String;

import javax.validation.constraints.NotNull;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "abstractorderentry")
public abstract class AbstractOrderEntry extends Item {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "abstractorderentry";
    public static final String PROPERTY_PRODUCT = "product";
    public static final String PROPERTY_ORDER = "order";
    @NotNull
    @Property(readable = true, unique = true, writable = true)
    protected Product product;

    /**
     * The categories the product is referenced by.
     */
    @Relation(relationName = "AbstractOrder2AbstractOrderEntry", mappedTo = "entries", type = io.spotnext.core.infrastructure.type.RelationType.ManyToOne, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.TARGET)
    @Property(readable = true, writable = true)
    public AbstractOrder order;

    @Accessor(propertyName = "product", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setProduct(Product product) {
        this.product = product;
    }

    @Accessor(propertyName = "product", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Product getProduct() {
        return this.product;
    }

    /**
     * The categories the product is referenced by.
     */
    @Accessor(propertyName = "order", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public AbstractOrder getOrder() {
        return this.order;
    }

    /**
     * The categories the product is referenced by.
     */
    @Accessor(propertyName = "order", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setOrder(AbstractOrder order) {
        this.order = order;
    }
}
