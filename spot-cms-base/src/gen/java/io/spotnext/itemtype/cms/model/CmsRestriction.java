/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.cms.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.infrastructure.annotation.Relation;

import io.spotnext.itemtype.cms.model.AbstractCmsItem;

import java.io.Serializable;

import java.lang.String;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "cmsrestriction")
public class CmsRestriction extends AbstractCmsItem {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "cmsrestriction";
    public static final String PROPERTY_EVALUATOR = "evaluator";
    public static final String PROPERTY_EVALUATOR_SCRIPT = "evaluatorScript";
    public static final String PROPERTY_CMS_ITEM = "cmsItem";

    /**
     * The spring bean id of the corresponding evaluator implementation.
     */
    @Property(readable = true, writable = true)
    protected String evaluator;

    /**
     * The beanshell script that evaluates the cms item visibility.
     */
    @Property(readable = true, writable = true)
    protected String evaluatorScript;
    @Relation(relationName = "AbstractCmsItem2CmsRestriction", mappedTo = "cmsRestrictions", type = io.spotnext.core.infrastructure.type.RelationType.ManyToOne, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.TARGET)
    @Property(readable = true, writable = true)
    public AbstractCmsItem cmsItem;

    @Accessor(propertyName = "cmsItem", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public AbstractCmsItem getCmsItem() {
        return this.cmsItem;
    }

    /**
     * The beanshell script that evaluates the cms item visibility.
     */
    @Accessor(propertyName = "evaluatorScript", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setEvaluatorScript(String evaluatorScript) {
        this.evaluatorScript = evaluatorScript;
    }

    @Accessor(propertyName = "cmsItem", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setCmsItem(AbstractCmsItem cmsItem) {
        this.cmsItem = cmsItem;
    }

    /**
     * The spring bean id of the corresponding evaluator implementation.
     */
    @Accessor(propertyName = "evaluator", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getEvaluator() {
        return this.evaluator;
    }

    /**
     * The beanshell script that evaluates the cms item visibility.
     */
    @Accessor(propertyName = "evaluatorScript", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getEvaluatorScript() {
        return this.evaluatorScript;
    }

    /**
     * The spring bean id of the corresponding evaluator implementation.
     */
    @Accessor(propertyName = "evaluator", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setEvaluator(String evaluator) {
        this.evaluator = evaluator;
    }
}
