/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.cms.model;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@ItemType(typeCode = "abstractnavigationentry")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class AbstractNavigationEntry extends CmsComponent {
    private static final long serialVersionUID = -1L;

    /** The navigation entry name. */
    @Property
    protected String name;

    @GetProperty
    public String getName() {
        return this.name;
    }

    @SetProperty
    public void setName(String name) {
        this.name = name;
        markAsDirty("name");
    }
}
