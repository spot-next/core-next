package io.spotnext.cms.rendering.view;

public class View {
	private ViewContext context;
	private String templateScript = "";

	public String getTemplateScript() {
		return templateScript;
	}

	public void setTemplateScript(final String templateScript) {
		this.templateScript = templateScript;
	}

	public ViewContext getContext() {
		return context;
	}

	public void setContext(final ViewContext context) {
		this.context = context;
	}

}
