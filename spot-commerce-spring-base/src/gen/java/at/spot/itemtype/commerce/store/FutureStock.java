/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.commerce.store;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.validation.constraints.NotNull;


@ItemType(typeCode = "futurestock")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class FutureStock extends Stock {
    private static final long serialVersionUID = -1L;

    /** Defines a future stock and when it will be available. */
    @Property
    @NotNull
    protected String availableAt;

    @GetProperty
    public String getAvailableAt() {
        return this.availableAt;
    }

    @SetProperty
    public void setAvailableAt(String availableAt) {
        this.availableAt = availableAt;
        markAsDirty("availableAt");
    }
}
