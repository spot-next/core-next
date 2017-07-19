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


/**
* This type can be used to store a configuration entry.
 */
@ItemType(typeCode = "configentry")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ConfigEntry extends UniqueIdItem {
    private static final long serialVersionUID = -1L;

    /** The short description of the configuration entry#s purpose. */
    @Property
    protected String description;
    @Property
    protected Double doubleValue;
    @Property
    protected Float floatValue;
    @Property
    protected Integer intValue;
    @Property
    protected Long longValue;
    @Property
    protected String stringValue;

    @GetProperty
    public String getDescription() {
        return this.description;
    }

    @GetProperty
    public Double getDoubleValue() {
        return this.doubleValue;
    }

    @GetProperty
    public Float getFloatValue() {
        return this.floatValue;
    }

    @GetProperty
    public Integer getIntValue() {
        return this.intValue;
    }

    @GetProperty
    public Long getLongValue() {
        return this.longValue;
    }

    @GetProperty
    public String getStringValue() {
        return this.stringValue;
    }

    @SetProperty
    public void setDescription(String description) {
        this.description = description;
        markAsDirty("description");
    }

    @SetProperty
    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
        markAsDirty("doubleValue");
    }

    @SetProperty
    public void setFloatValue(Float floatValue) {
        this.floatValue = floatValue;
        markAsDirty("floatValue");
    }

    @SetProperty
    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
        markAsDirty("intValue");
    }

    @SetProperty
    public void setLongValue(Long longValue) {
        this.longValue = longValue;
        markAsDirty("longValue");
    }

    @SetProperty
    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
        markAsDirty("stringValue");
    }
}
