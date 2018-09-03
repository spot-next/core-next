package io.spotnext.cms.strategy.impl;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.cms.annotations.Renderable;
import io.spotnext.cms.exception.PageNotFoundException;
import io.spotnext.cms.strategy.TemplateRenderStrategy;
import io.spotnext.core.infrastructure.http.ModelAndView;
import io.spotnext.itemtype.cms.CmsPage;
import io.spotnext.itemtype.cms.enumeration.TemplateRenderEngine;

/**
 * <p>
 * ThymeleafTemplateRenderStrategy class.
 * </p>
 */
@Service
@SuppressFBWarnings(value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR", justification = "initialized by spring")
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

	private SpringTemplateEngine templateEngine;

	/**
	 * Constructs a thymeleaf template engine.
	 */
	@PostConstruct
	public void setup() {
		final ITemplateResolver templateResolver = createDefaultTemplateResolver(templateFolder, templateExtension);
		templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		templateEngine.setEnableSpringELCompiler(true);
		templateEngine.addDialect(new Java8TimeDialect());
	}

	/**
	 * <p>
	 * createDefaultTemplateResolver.
	 * </p>
	 *
	 * @param templateFolder    a {@link java.lang.String} object.
	 * @param templateExtension a {@link java.lang.String} object.
	 * @return a {@link org.thymeleaf.templateresolver.ITemplateResolver} object.
	 */
	protected ITemplateResolver createDefaultTemplateResolver(final String templateFolder,
			final String templateExtension) {
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
		templateResolver
				.setSuffix(StringUtils.isNotBlank(templateExtension) ? templateExtension : DEFAULT_TEMPLATE_EXCENTIONS);
		templateResolver.setCacheTTLMs(2000l);
		templateResolver.setCacheable(cacheEnabled);

		templateResolver.setCacheTTLMs(DEFAULT_CACHE_TTL_MS);

		return templateResolver;
	}

	@Override
	public ModelAndView prepareCmsPage(final CmsPage page, final Map<String, Object> context)
			throws PageNotFoundException {

		// create a new context and put both the page item (converted to a map,
		// and the
		// given context) into it
		final Map<String, Object> mergedContext = new HashMap<>();

		// first include the layout renderable properties of the page
		mergedContext.putAll(page.getLayout().getProperties((f, v) -> f.isAnnotationPresent(Renderable.class)));

		// then let the page properties overwrite the layout properties, if not
		// null!
		mergedContext.putAll(page.getProperties((f, v) -> f.isAnnotationPresent(Renderable.class) && v != null));

		// finally add the given context
		if (context != null) {
			mergedContext.putAll(context);
		}

		// the "main entry point" for the rendered is the page.html template. It
		// renders the layout and the page.
		// for this to work, the current page needs to be references with "this"
		mergedContext.put("this", page);

		return ModelAndView.ok("page").withPayload(mergedContext);
	}

	/**
	 * {@inheritDoc} Process the specified template (usually the template name). Output will be written into a String that will be returned from calling this
	 * method, once template processing has finished.
	 */
	@Override
	public String renderTemplate(final String templateName, final Object context) {
		Map<String, Object> renderingContext = null;

		if (context instanceof Map) {
			renderingContext = (Map<String, Object>) context;
		} else {
			if (context instanceof spark.ModelAndView) {
				renderingContext = objectMapper.convertValue(((spark.ModelAndView) context).getModel(), Map.class);
			} else {
				renderingContext = objectMapper.convertValue(context, Map.class);
			}
		}

		final Context ctx = new Context(Locale.getDefault(), renderingContext);

		return templateEngine.process(templateName, ctx);
	}

	@Override
	public TemplateRenderEngine supportsEngine() {
		return TemplateRenderEngine.THYMELEAF;
	}

}
