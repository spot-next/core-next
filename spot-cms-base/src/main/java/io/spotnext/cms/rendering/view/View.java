package io.spotnext.cms.rendering.view;

/**
 * <p>View class.</p>
 */
public class View {
	private ViewContext context;
	private String templateScript = "";

	/**
	 * <p>Getter for the field <code>templateScript</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTemplateScript() {
		return templateScript;
	}

	/**
	 * <p>Setter for the field <code>templateScript</code>.</p>
	 *
	 * @param templateScript a {@link java.lang.String} object.
	 */
	public void setTemplateScript(final String templateScript) {
		this.templateScript = templateScript;
	}

	/**
	 * <p>Getter for the field <code>context</code>.</p>
	 *
	 * @return a {@link io.spotnext.cms.rendering.view.ViewContext} object.
	 */
	public ViewContext getContext() {
		return context;
	}

	/**
	 * <p>Setter for the field <code>context</code>.</p>
	 *
	 * @param context a {@link io.spotnext.cms.rendering.view.ViewContext} object.
	 */
	public void setContext(final ViewContext context) {
		this.context = context;
	}

}
