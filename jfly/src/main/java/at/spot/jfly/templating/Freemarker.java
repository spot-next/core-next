package at.spot.jfly.templating;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class Freemarker {
	Configuration cfg = null;

	public Freemarker() throws IOException {
		cfg = new Configuration(Configuration.VERSION_2_3_25);

		File templateFolder = null;
		try {
			templateFolder = new File(this.getClass().getClassLoader().getResource("template/").toURI());
		} catch (final URISyntaxException e) {
			throw new IOException("Cannot load template folder path.", e);
		}

		cfg.setDirectoryForTemplateLoading(templateFolder);
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
		cfg.setLogTemplateExceptions(false);
	}

	public String renderTemplate() {
		return null;
	}

}
