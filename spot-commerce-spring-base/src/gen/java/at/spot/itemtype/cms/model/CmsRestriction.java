/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.cms.model;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@ItemType(typeCode = "cmsrestriction")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class CmsRestriction extends AbstractCmsItem {
    private static final long serialVersionUID = -1L;

    /** The spring bean id of the corresponding evaluator implementation. */
    @Property
    protected String evaluator;

    /** The beanshell script that evaluates the cms item visibility. */
    @Property
    protected String evaluatorScript;

    @GetProperty
    public String getEvaluator() {
        return this.evaluator;
    }

    @GetProperty
    public String getEvaluatorScript() {
        return this.evaluatorScript;
    }

    @SetProperty
    public void setEvaluator(String evaluator) {
        this.evaluator = evaluator;
        markAsDirty("evaluator");
    }

    @SetProperty
    public void setEvaluatorScript(String evaluatorScript) {
        this.evaluatorScript = evaluatorScript;
        markAsDirty("evaluatorScript");
    }
}
