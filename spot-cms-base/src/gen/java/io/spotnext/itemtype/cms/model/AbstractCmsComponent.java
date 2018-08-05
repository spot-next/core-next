/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.cms.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.infrastructure.annotation.Relation;

import io.spotnext.itemtype.cms.enumeration.TemplateRenderEngine;
import io.spotnext.itemtype.cms.model.AbstractCmsContainerComponent;
import io.spotnext.itemtype.cms.model.AbstractCmsItem;

import java.io.Serializable;

import java.lang.String;

import java.util.Set;

import javax.validation.constraints.NotNull;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "abstractcmscomponent")
public abstract class AbstractCmsComponent extends AbstractCmsItem {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "abstractcmscomponent";
    public static final String PROPERTY_RENDER_CONTEXT_PROVIDER = "renderContextProvider";
    public static final String PROPERTY_RENDER_CONTEXT_PREPARATION_SCRIPT = "renderContextPreparationScript";
    public static final String PROPERTY_CONTENT = "content";
    public static final String PROPERTY_RENDER_ENGINE = "renderEngine";
    public static final String PROPERTY_CONTAINER = "container";

    /**
     * The spring bean id of the context provider implementation.
     */
    @Property(readable = true, writable = true)
    protected String renderContextProvider;

    /**
     * Beanshell code that is executed before the page rendering.<br>                                It can be used to prepare the render context variables.
     */
    @Property(readable = true, writable = true)
    protected String renderContextPreparationScript;
    @Property(readable = true, writable = true)
    protected String content;
    @NotNull
    @Property(readable = true, writable = true)
    protected TemplateRenderEngine renderEngine;
    @Property(readable = true, writable = true)
    @Relation(collectionType = io.spotnext.core.infrastructure.type.RelationCollectionType.Set, relationName = "AbstractCmsContainerComponent2AbstractCmsComponent", mappedTo = "components", type = io.spotnext.core.infrastructure.type.RelationType.ManyToMany, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.TARGET)
    public Set<AbstractCmsContainerComponent> container;

    /**
     * Beanshell code that is executed before the page rendering.<br>                                It can be used to prepare the render context variables.
     */
    @Accessor(propertyName = "renderContextPreparationScript", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setRenderContextPreparationScript(
        String renderContextPreparationScript) {
        this.renderContextPreparationScript = renderContextPreparationScript;
    }

    @Accessor(propertyName = "renderEngine", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setRenderEngine(TemplateRenderEngine renderEngine) {
        this.renderEngine = renderEngine;
    }

    @Accessor(propertyName = "content", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setContent(String content) {
        this.content = content;
    }

    @Accessor(propertyName = "container", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setContainer(Set<AbstractCmsContainerComponent> container) {
        this.container = container;
    }

    @Accessor(propertyName = "renderEngine", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public TemplateRenderEngine getRenderEngine() {
        return this.renderEngine;
    }

    /**
     * Beanshell code that is executed before the page rendering.<br>                                It can be used to prepare the render context variables.
     */
    @Accessor(propertyName = "renderContextPreparationScript", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getRenderContextPreparationScript() {
        return this.renderContextPreparationScript;
    }

    @Accessor(propertyName = "container", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<AbstractCmsContainerComponent> getContainer() {
        return this.container;
    }

    /**
     * The spring bean id of the context provider implementation.
     */
    @Accessor(propertyName = "renderContextProvider", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getRenderContextProvider() {
        return this.renderContextProvider;
    }

    /**
     * The spring bean id of the context provider implementation.
     */
    @Accessor(propertyName = "renderContextProvider", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setRenderContextProvider(String renderContextProvider) {
        this.renderContextProvider = renderContextProvider;
    }

    @Accessor(propertyName = "content", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public String getContent() {
        return this.content;
    }
}
