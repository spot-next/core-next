package io.spotnext.cms.service.impl;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.cms.exception.PageNotFoundException;
import io.spotnext.cms.rendering.view.View;
import io.spotnext.cms.rendering.view.ViewContext;
import io.spotnext.cms.restriction.RestrictionEvaluationResult;
import io.spotnext.cms.service.CmsPageService;
import io.spotnext.cms.service.CmsRenderService;
import io.spotnext.cms.service.CmsRestrictionService;
import io.spotnext.core.infrastructure.service.I18nService;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.itemtype.cms.enumeration.TemplateRenderEngine;
import io.spotnext.itemtype.cms.model.CmsPage;
import io.spotnext.itemtype.cms.model.CmsPageTemplate;

public class DefaultCmsPageService implements CmsPageService {

	@Autowired
	protected Map<TemplateRenderEngine, CmsRenderService> renderServices;

	@Autowired
	protected CmsRestrictionService cmsRestrictionService;

	@Autowired
	protected QueryService queryService;

	@Autowired
	protected I18nService i18nService;

	@Override
	public CmsPage getPageById(final String pageId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CmsPageTemplate getPageTemplateById(final String pageTemplateId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String renderPage(final CmsPage page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CmsPage getPageForUrl(final String url) {
		return null;
	}

	@Override
	public void renderRequest(final HttpServletRequest request, final HttpServletResponse response) {
		final CmsPage page = getPageForUrl(request.getRequestURI());

		Exception exception = null;

		// evaluate restrictions on page
		final RestrictionEvaluationResult result = cmsRestrictionService.checkRestrictions(page);

		if (result.isAllowed() && page != null) {
			final View view = new View();
			final ViewContext context = new ViewContext();

			view.setContext(context);
			view.setTemplateScript(getTemplateScript(page));

			if (page.getTemplate() != null) {
				final CmsPageTemplate template = page.getTemplate();

				context.add("faviconPath", template.getFavIconPath().get(i18nService.getCurrentLocale()));

				if (template.getFavIcon() != null) {
					context.add("faviconPath", template.getFavIcon().getDataPath());
				}

				context.add("metaTags", template.getMetaTags());
				context.add("pageTitle", template.getTitle());
			}

			// override data from page
			if (StringUtils.isNotBlank(page.getFavIconPath().get(i18nService.getCurrentLocale()))) {
				context.add("faviconPath", page.getFavIconPath());
			}

			if (page.getFavIcon() != null) {
				context.add("faviconPath", page.getFavIcon().getDataPath());
			}

			// context.add("metaTags", template.getMetaTags());
			// context.add("pageTitle", template.getTitle());
			// render actual page request
			final CmsRenderService renderService = renderServices.get(page.getRenderEngine());
			try {
				renderService.renderView(view, request, response.getOutputStream());
			} catch (final IOException e) {
				exception = e;
			}
		}

		if (exception == null) {
			exception = new PageNotFoundException("Requested URL not found");
		}

		// render 404
		renderErrorPage(request, response, page, exception);
	}

	protected void renderErrorPage(final HttpServletRequest request, final HttpServletResponse response,
			final CmsPage page, final Exception e) {

	}

	protected String getTemplateScript(final CmsPage page) {
		String templateScript = "";

		if (StringUtils.isNotBlank(page.getContent())) {
			templateScript = page.getContent();
		} else if (page.getTemplate() != null && StringUtils.isNotBlank(page.getTemplate().getContent())) {
			templateScript = page.getTemplate().getContent();
		}

		return templateScript;
	}
}
