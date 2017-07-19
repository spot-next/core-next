/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.cms.model;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.Map;


@ItemType(typeCode = "contentslot")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ContentSlot extends AbstractCmsItem {
    private static final long serialVersionUID = -1L;
    @Property
    protected Map<String, CmsComponent> cmsComponents;

    @GetProperty
    public Map<String, CmsComponent> getCmsComponents() {
        return this.cmsComponents;
    }

    @SetProperty
    public void setCmsComponents(Map<String, CmsComponent> cmsComponents) {
        this.cmsComponents = cmsComponents;
        markAsDirty("cmsComponents");
    }
}
