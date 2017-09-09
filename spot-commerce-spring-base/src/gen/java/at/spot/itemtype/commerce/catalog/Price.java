/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.commerce.catalog;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.core.model.Item;

import at.spot.itemtype.core.internationalization.Currency;

import at.spot.itemtype.core.user.User;
import at.spot.itemtype.core.user.UserGroup;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.math.BigDecimal;

import javax.persistence.Entity;

import javax.validation.constraints.NotNull;


/**
* This is the container for all categories and products.
 */
@ItemType(typeCode = "price")
@Entity
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Price extends Item {
    private static final long serialVersionUID = -1L;

    /** The currency of the price. */
    @Property
    protected Currency currency;

    /** Defines if the price is net or gross. */
    @Property
    protected boolean net = true;

    /** The price value. */
    @Property
    protected BigDecimal price;

    /** The product id the price is used for. */
    @Property(unique = true)
    @NotNull
    protected String productId;

    /** The assigned user. */
    @Property
    protected User user;

    /** The assigned user group. */
    @Property
    protected UserGroup userGroup;

    @GetProperty
    public Currency getCurrency() {
        return this.currency;
    }

    @GetProperty
    public boolean getNet() {
        return this.net;
    }

    @GetProperty
    public BigDecimal getPrice() {
        return this.price;
    }

    @GetProperty
    public String getProductId() {
        return this.productId;
    }

    @GetProperty
    public User getUser() {
        return this.user;
    }

    @GetProperty
    public UserGroup getUserGroup() {
        return this.userGroup;
    }

    @SetProperty
    public void setCurrency(Currency currency) {
        this.currency = currency;
        markAsDirty("currency");
    }

    @SetProperty
    public void setNet(boolean net) {
        this.net = net;
        markAsDirty("net");
    }

    @SetProperty
    public void setPrice(BigDecimal price) {
        this.price = price;
        markAsDirty("price");
    }

    @SetProperty
    public void setProductId(String productId) {
        this.productId = productId;
        markAsDirty("productId");
    }

    @SetProperty
    public void setUser(User user) {
        this.user = user;
        markAsDirty("user");
    }

    @SetProperty
    public void setUserGroup(UserGroup userGroup) {
        this.userGroup = userGroup;
        markAsDirty("userGroup");
    }
}
