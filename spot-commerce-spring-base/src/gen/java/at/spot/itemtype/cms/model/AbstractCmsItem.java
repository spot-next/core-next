/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.cms.model;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.itemtype.core.UniqueIdItem;

import at.spot.itemtype.core.catalog.Catalog;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import javax.validation.constraints.NotNull;


@ItemType(typeCode = "abstractcmsitem")
@Entity
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class AbstractCmsItem extends UniqueIdItem {
    private static final long serialVersionUID = -1L;

    /** The content catalog of the item. */
    @Property(unique = true)
    @NotNull
    protected Catalog catalog;

    /** f set to true, only one restriction must evaluate to "show cms item"  for the item to be visible. */
    @Property
    protected boolean onlyOneRestrictionMustApply;

    /** The restrictions evaluate if the current cms item should be rendered. */
    @Property
    @ElementCollection
    protected List<CmsRestriction> restrictions;

    @GetProperty
    public Catalog getCatalog() {
        return this.catalog;
    }

    @GetProperty
    public boolean getOnlyOneRestrictionMustApply() {
        return this.onlyOneRestrictionMustApply;
    }

    @GetProperty
    public List<CmsRestriction> getRestrictions() {
        return this.restrictions;
    }

    @SetProperty
    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
        markAsDirty("catalog");
    }

    @SetProperty
    public void setOnlyOneRestrictionMustApply(boolean onlyOneRestrictionMustApply) {
        this.onlyOneRestrictionMustApply = onlyOneRestrictionMustApply;
        markAsDirty("onlyOneRestrictionMustApply");
    }

    @SetProperty
    public void setRestrictions(List<CmsRestriction> restrictions) {
        this.restrictions = restrictions;
        markAsDirty("restrictions");
    }
}
