/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.commerce.order;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.infrastructure.annotation.Relation;
import io.spotnext.core.infrastructure.support.ItemCollectionFactory;

import io.spotnext.itemtype.commerce.customer.Customer;
import io.spotnext.itemtype.commerce.order.AbstractOrderEntry;
import io.spotnext.itemtype.core.UniqueIdItem;

import java.io.Serializable;

import java.lang.String;

import java.util.Set;

import javax.validation.constraints.NotNull;


/**
 * The abstract base type for orders and carts.
 */
@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "abstractorder")
public abstract class AbstractOrder extends UniqueIdItem {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "abstractorder";
    public static final String PROPERTY_CUSTOMER = "customer";
    public static final String PROPERTY_ENTRIES = "entries";
    @Property(readable = true, unique = true, writable = true)
    @NotNull
    protected Customer customer;

    /**
     * The categories the product is referenced by.
     */
    @Relation(collectionType = io.spotnext.core.infrastructure.type.RelationCollectionType.Set, relationName = "AbstractOrder2AbstractOrderEntry", mappedTo = "order", type = io.spotnext.core.infrastructure.type.RelationType.OneToMany, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.SOURCE)
    @Property(readable = true, writable = true)
    public Set<AbstractOrderEntry> entries;

    @Accessor(propertyName = "customer", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * The categories the product is referenced by.
     */
    @Accessor(propertyName = "entries", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<AbstractOrderEntry> getEntries() {
        return ItemCollectionFactory.wrap(this, "entries", this.entries);
    }

    /**
     * The categories the product is referenced by.
     */
    @Accessor(propertyName = "entries", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setEntries(Set<AbstractOrderEntry> entries) {
        this.entries = entries;
    }

    @Accessor(propertyName = "customer", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Customer getCustomer() {
        return this.customer;
    }
}
