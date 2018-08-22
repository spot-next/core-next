package io.spotnext.cms.strategy.impl;

import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.cms.strategy.TemplateRenderStrategy;
import spark.ModelAndView;

@Service
@SuppressFBWarnings(value="UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", justification="initialized by spring")
public class ThymeleafTemplateRenderStrategy implements TemplateRenderStrategy {

	private static final String DEFAULT_TEMPLATE_FOLDER = "templates/";
	private static final String DEFAULT_TEMPLATE_EXCENTIONS = ".html";
	private static final long DEFAULT_CACHE_TTL_MS = 3600000L;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Value("${cms.templaterendering.templatefolder:/templates}")
	private String templateFolder;

	@Value("${cms.templaterendering.templateextension:.html}")
	private String templateExtension;

	@Value("${service.templaterenderer.thymeleaf.cache:false}")
	private boolean cacheEnabled;

	private org.thymeleaf.TemplateEngine templateEngine;

	/**
	 * Constructs a thymeleaf template engine.
	 */
	@PostConstruct
	public void setup() {
		final ITemplateResolver templateResolver = createDefaultTemplateResolver(templateFolder, templateExtension);
		templateEngine = new org.thymeleaf.TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.addDialect(new Java8TimeDialect());
	}

	protected ITemplateResolver createDefaultTemplateResolver(final String templateFolder, final String templateExtension) {
		final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setTemplateMode(TemplateMode.HTML);

		final String folder;

		// sanitize the folder path or use the default if not set
		if (StringUtils.isNotBlank(templateFolder)) {
			folder = templateFolder + (!templateFolder.endsWith("/") ? "/" : "");
		} else {
			folder = DEFAULT_TEMPLATE_FOLDER;
		}

		templateResolver.setPrefix(folder);
		templateResolver.setSuffix(StringUtils.isNotBlank(templateExtension) ? templateExtension : DEFAULT_TEMPLATE_EXCENTIONS);
		templateResolver.setCacheTTLMs(2000l);
		templateResolver.setCacheable(cacheEnabled);

		templateResolver.setCacheTTLMs(DEFAULT_CACHE_TTL_MS);

		return templateResolver;
	}

	/**
	 * Process the specified template (usually the template name). Output will
	 * be written into a String that will be returned from calling this method,
	 * once template processing has finished.
	 * 
	 * @param templateName
	 *            the name of the template, will be resolved from resources
	 *            folder on the class path
	 * @param context
	 *            the context holding the template variables
	 * @return rendered template
	 */
	@Override
	public String renderTemplate(final String templateName, final Object context) {
		Map<String, Object> renderingContext = null;

		if (context instanceof Map) {
			renderingContext = (Map<String, Object>) context;
		} else {
			if (context instanceof ModelAndView) {
				renderingContext = objectMapper.convertValue(((ModelAndView) context).getModel(), Map.class);
			} else {
				renderingContext = objectMapper.convertValue(context, Map.class);
			}
		}

		final Context ctx = new Context(Locale.getDefault(), renderingContext);

		return templateEngine.process(templateName, ctx);
	}

}
