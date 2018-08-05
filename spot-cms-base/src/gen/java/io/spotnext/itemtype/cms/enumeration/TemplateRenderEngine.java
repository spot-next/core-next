/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.cms.enumeration;


/**
 * The render engine used to render cms items.
 */
@SuppressWarnings("unchecked")
public enum TemplateRenderEngine {VELOCITY("VELOCITY"),
    FREEMARKER("FREEMARKER"),
    JSP("JSP");

    private String internalValue;

    private TemplateRenderEngine(String internalValue) {
        this.internalValue = internalValue;
    }

    /**
     * Returns the internal value of the current enum.
     */
    public String getInternalName() {
        return this.internalValue;
    }
}
