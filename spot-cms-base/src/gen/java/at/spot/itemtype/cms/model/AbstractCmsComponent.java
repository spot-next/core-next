/**
 * This file is auto-generated. All changes will be overwritten.
 */
package at.spot.itemtype.cms.model;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.SetProperty;

import at.spot.itemtype.cms.enumeration.TemplateRenderEngine;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.persistence.Entity;

import javax.validation.constraints.NotNull;


@ItemType(typeCode = "abstractcmscomponent")
@Entity
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public abstract class AbstractCmsComponent extends AbstractCmsItem {
    private static final long serialVersionUID = -1L;
    @Property
    protected String content;

    /** Beanshell code that is executed before the page rendering. It can be used to prepare the render context variables. */
    @Property
    protected String renderContextPreparationScript;

    /** The spring bean id of the context provider implementation. */
    @Property
    protected String renderContextProvider;
    @Property
    @NotNull
    protected TemplateRenderEngine renderEngine;

    @GetProperty
    public String getContent() {
        return this.content;
    }

    @GetProperty
    public String getRenderContextPreparationScript() {
        return this.renderContextPreparationScript;
    }

    @GetProperty
    public String getRenderContextProvider() {
        return this.renderContextProvider;
    }

    @GetProperty
    public TemplateRenderEngine getRenderEngine() {
        return this.renderEngine;
    }

    @SetProperty
    public void setContent(String content) {
        this.content = content;
        markAsDirty("content");
    }

    @SetProperty
    public void setRenderContextPreparationScript(String renderContextPreparationScript) {
        this.renderContextPreparationScript = renderContextPreparationScript;
        markAsDirty("renderContextPreparationScript");
    }

    @SetProperty
    public void setRenderContextProvider(String renderContextProvider) {
        this.renderContextProvider = renderContextProvider;
        markAsDirty("renderContextProvider");
    }

    @SetProperty
    public void setRenderEngine(TemplateRenderEngine renderEngine) {
        this.renderEngine = renderEngine;
        markAsDirty("renderEngine");
    }
}
