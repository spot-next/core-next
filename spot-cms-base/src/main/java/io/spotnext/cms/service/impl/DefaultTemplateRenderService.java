package io.spotnext.cms.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.cms.exception.PageNotFoundException;
import io.spotnext.cms.exception.TemplateRenderException;
import io.spotnext.cms.service.CmsPageService;
import io.spotnext.cms.service.TemplateRenderService;
import io.spotnext.cms.strategy.TemplateRenderStrategy;
import io.spotnext.cms.strategy.impl.ThymeleafTemplateRenderStrategy;
import io.spotnext.core.infrastructure.http.ModelAndView;
import io.spotnext.itemtype.cms.CmsPage;
import io.spotnext.itemtype.cms.enumeration.TemplateRenderEngine;

/**
 * <p>
 * DefaultTemplateRenderService class.
 * </p>
 */
@Service
public class DefaultTemplateRenderService implements TemplateRenderService {

	@Autowired
	protected List<TemplateRenderStrategy> templateRenderStrategies;

	@Autowired
	private ThymeleafTemplateRenderStrategy thymeleafTemplateRenderStrategy;

	@Autowired
	private CmsPageService cmsPageService;

	/** {@inheritDoc} */
	@Override
	public String renderTemplate(TemplateRenderEngine engine, String templateName, Object context)
			throws TemplateRenderException {

		return getStrategy(engine).renderTemplate(templateName, context);
	}

	@Override
	public ModelAndView prepareCmsPage(String pageId, Map<String, Object> context)
			throws PageNotFoundException, TemplateRenderException {

		final CmsPage page = cmsPageService.getPageById(pageId);
		final TemplateRenderStrategy strategy = getStrategy(page.getRenderEngine());
		return strategy.prepareCmsPage(page, context);
	}

	private TemplateRenderStrategy getStrategy(TemplateRenderEngine engine) throws TemplateRenderException {
		Optional<TemplateRenderStrategy> strategy = templateRenderStrategies.stream()
				.filter(s -> engine.equals(s.supportsEngine())).findFirst();

		if (strategy.isPresent()) {
			return strategy.get();
		}

		throw new TemplateRenderException(
				String.format("No suitable TemplateRenderStrategy was found for engine '%s'.", engine));
	}
}
