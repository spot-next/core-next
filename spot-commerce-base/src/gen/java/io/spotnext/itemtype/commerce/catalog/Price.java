/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.commerce.catalog;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.types.Item;

import io.spotnext.itemtype.core.internationalization.Currency;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

import java.io.Serializable;

import java.lang.Boolean;
import java.lang.String;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;


/**
 * This is the container for all categories and products.
 */
@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "price")
public class Price extends Item {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "price";
    public static final String PROPERTY_PRODUCT_ID = "productId";
    public static final String PROPERTY_USER_GROUP = "userGroup";
    public static final String PROPERTY_USER = "user";
    public static final String PROPERTY_PRICE = "price";
    public static final String PROPERTY_CURRENCY = "currency";
    public static final String PROPERTY_NET = "net";

    /**
     * The product id the price is used for.
     */
    @NotNull
    @Property(readable = true, unique = true, writable = true)
    protected String productId;

    /**
     * The assigned user group.
     */
    @Property(readable = true, writable = true)
    protected UserGroup userGroup;

    /**
     * The assigned user.
     */
    @Property(readable = true, writable = true)
    protected User user;

    /**
     * The price value.
     */
    @Property(readable = true, writable = true)
    protected BigDecimal price;

    /**
     * The currency of the price.
     */
    @Property(readable = true, writable = true)
    protected Currency currency;

    /**
     * Defines if the price is net or gross.
     */
    @Property(readable = true, writable = true)
    protected Boolean net = true;

    /**
     * The product id the price is used for.
     */
    @Accessor(propertyName = "productId", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getProductId() {
        return this.productId;
    }

    /**
     * The currency of the price.
     */
    @Accessor(propertyName = "currency", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Currency getCurrency() {
        return this.currency;
    }

    /**
     * The assigned user group.
     */
    @Accessor(propertyName = "userGroup", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public UserGroup getUserGroup() {
        return this.userGroup;
    }

    /**
     * Defines if the price is net or gross.
     */
    @Accessor(propertyName = "net", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Boolean getNet() {
        return this.net;
    }

    /**
     * The assigned user.
     */
    @Accessor(propertyName = "user", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * The assigned user.
     */
    @Accessor(propertyName = "user", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public User getUser() {
        return this.user;
    }

    /**
     * The price value.
     */
    @Accessor(propertyName = "price", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public BigDecimal getPrice() {
        return this.price;
    }

    /**
     * The price value.
     */
    @Accessor(propertyName = "price", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * The currency of the price.
     */
    @Accessor(propertyName = "currency", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * The product id the price is used for.
     */
    @Accessor(propertyName = "productId", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * Defines if the price is net or gross.
     */
    @Accessor(propertyName = "net", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setNet(Boolean net) {
        this.net = net;
    }

    /**
     * The assigned user group.
     */
    @Accessor(propertyName = "userGroup", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
    }
}
