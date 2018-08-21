package io.spotnext.cms.strategy.impl;

import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.spotnext.cms.strategy.TemplateRenderStrategy;

@Service
public class ThymeleafTemplateRenderStrategy implements TemplateRenderStrategy {

	private static final String DEFAULT_PREFIX = "templates/";
	private static final String DEFAULT_SUFFIX = ".html";
	private static final long DEFAULT_CACHE_TTL_MS = 3600000L;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${cms.templaterendering.templatefolder:resources/templates}")
	private String templateFolder;

	@Value("${cms.templaterendering.templateextension:html}")
	private String templateExtension;

	private org.thymeleaf.TemplateEngine templateEngine;

	/**
	 * Constructs a thymeleaf template engine.
	 */
	@PostConstruct
	public void setup() {
		ITemplateResolver templateResolver = createDefaultTemplateResolver(templateFolder, templateExtension);
		templateEngine = new org.thymeleaf.TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.addDialect(new Java8TimeDialect());
	}

	private static ITemplateResolver createDefaultTemplateResolver(String prefix, String suffix) {
		final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setTemplateMode(TemplateMode.HTML);

		templateResolver.setPrefix(prefix != null ? prefix : DEFAULT_PREFIX);
		templateResolver.setSuffix(suffix != null ? suffix : DEFAULT_SUFFIX);

		templateResolver.setCacheTTLMs(DEFAULT_CACHE_TTL_MS);
		return templateResolver;
	}

	/**
	 * Process the specified template (usually the template name). Output will be
	 * written into a String that will be returned from calling this method, once
	 * template processing has finished.
	 * 
	 * @param templateName the name of the template, will be resolved from resources
	 *                     folder on the class path
	 * @param context      the context holding the template variables
	 * @return rendered template
	 */
	@Override
	public String renderTemplate(String templateName, Object context) {
		Map<String, Object> renderingContext = null;

		if (context instanceof Map) {
			renderingContext = (Map<String, Object>) context;
		} else {
			renderingContext = objectMapper.convertValue(context, Map.class);
		}

		Context ctx = new Context(Locale.getDefault());
		ctx.setVariables(renderingContext);

		return templateEngine.process(templateName, ctx);
	}

}
