/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.core.configuration;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.itemtype.core.UniqueIdItem;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;


/**
* This type can be used to store a set of configuration entries.
 */
@ItemType(typeCode = "configuration")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Configuration extends UniqueIdItem {
    private static final long serialVersionUID = -1L;

    /** The short description of the configuration's purpose. */
    @Property
    protected String description;

    /** The config entries referenced by this configuration. */
    @Property
    protected List<ConfigEntry> entries;

    @GetProperty
    public String getDescription() {
        return this.description;
    }

    @GetProperty
    public List<ConfigEntry> getEntries() {
        return this.entries;
    }

    @SetProperty
    public void setDescription(String description) {
        this.description = description;
        markAsDirty("description");
    }

    @SetProperty
    public void setEntries(List<ConfigEntry> entries) {
        this.entries = entries;
        markAsDirty("entries");
    }
}
