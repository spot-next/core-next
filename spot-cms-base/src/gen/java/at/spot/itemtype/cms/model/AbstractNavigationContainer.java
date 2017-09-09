/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.cms.model;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.core.model.Item;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;


@ItemType(typeCode = "abstractnavigationcontainer")
@Entity
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class AbstractNavigationContainer extends Item {
    private static final long serialVersionUID = -1L;

    /** A list of all navigation link. */
    @Property
    @ElementCollection
    protected List<NavigationLinkEntry> entries;

    @GetProperty
    public List<NavigationLinkEntry> getEntries() {
        return this.entries;
    }

    @SetProperty
    public void setEntries(List<NavigationLinkEntry> entries) {
        this.entries = entries;
        markAsDirty("entries");
    }
}
