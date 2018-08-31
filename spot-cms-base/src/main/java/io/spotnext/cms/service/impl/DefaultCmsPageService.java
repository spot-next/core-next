package io.spotnext.cms.service.impl;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.cms.exception.PageNotFoundException;
import io.spotnext.cms.service.CmsPageService;
import io.spotnext.cms.service.CmsRenderService;
import io.spotnext.cms.service.CmsRestrictionService;
import io.spotnext.core.infrastructure.service.I18nService;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.itemtype.cms.CmsPage;
import io.spotnext.itemtype.cms.enumeration.TemplateRenderEngine;

/**
 * <p>
 * DefaultCmsPageService class.
 * </p>
 */
@Service
public class DefaultCmsPageService extends AbstractService implements CmsPageService {

//	@Autowired
	protected Map<TemplateRenderEngine, CmsRenderService> renderServices;

//	@Autowired
	protected CmsRestrictionService cmsRestrictionService;

	@Autowired
	protected QueryService queryService;

	@Autowired
	protected I18nService i18nService;

	@Override
	public CmsPage getPageById(String pageId) throws PageNotFoundException {
		final ModelQuery<CmsPage> query = new ModelQuery<>(CmsPage.class,
				Collections.singletonMap(CmsPage.PROPERTY_ID, pageId));
		final CmsPage page = getModelService().get(query);

		if (page != null) {
			return page;
		}

		throw new PageNotFoundException(String.format("Page with id '%s' not found.", pageId));
	}

//	/** {@inheritDoc} */
//	@Override
//	public CmsPage getPageById(final String pageId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/** {@inheritDoc} */
//	@Override
//	public CmsPageTemplate getPageTemplateById(final String pageTemplateId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/** {@inheritDoc} */
//	@Override
//	public String renderPage(final CmsPage page) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	/** {@inheritDoc} */
//	@Override
//	public CmsPage getPageForUrl(final String url) {
//		return null;
//	}

	/** {@inheritDoc} */
//	@Override
//	public void renderRequest(final HttpServletRequest request, final HttpServletResponse response) {
//		final CmsPage page = getPageForUrl(request.getRequestURI());
//
//		Exception exception = null;
//
//		// evaluate restrictions on page
//		final RestrictionEvaluationResult result = cmsRestrictionService.checkRestrictions(page);
//
//		if (result.isAllowed() && page != null) {
//			final View view = new View();
//			final ViewContext context = new ViewContext();
//
//			view.setContext(context);
//			view.setTemplateScript(getTemplateScript(page));
//
//			if (page.getTemplate() != null) {
//				final CmsPageTemplate template = page.getTemplate();
//
//				context.add("faviconPath", template.getFavIconPath().get(i18nService.getCurrentLocale()));
//
//				if (template.getFavIcon() != null) {
//					context.add("faviconPath", template.getFavIcon().getDataPath());
//				}
//
//				context.add("metaTags", template.getMetaTags());
//				context.add("pageTitle", template.getTitle());
//			}
//
//			// override data from page
//			if (StringUtils.isNotBlank(page.getFavIconPath().get(i18nService.getCurrentLocale()))) {
//				context.add("faviconPath", page.getFavIconPath());
//			}
//
//			if (page.getFavIcon() != null) {
//				context.add("faviconPath", page.getFavIcon().getDataPath());
//			}
//
//			// context.add("metaTags", template.getMetaTags());
//			// context.add("pageTitle", template.getTitle());
//			// render actual page request
//			final CmsRenderService renderService = renderServices.get(page.getRenderEngine());
//			try {
//				renderService.renderView(view, request, response.getOutputStream());
//			} catch (final IOException e) {
//				exception = e;
//			}
//		}
//
//		if (exception == null) {
//			exception = new PageNotFoundException("Requested URL not found");
//		}
//
//		// render 404
//		renderErrorPage(request, response, page, exception);
//	}

	/**
	 * <p>
	 * renderErrorPage.
	 * </p>
	 *
	 * @param request  a {@link javax.servlet.http.HttpServletRequest} object.
	 * @param response a {@link javax.servlet.http.HttpServletResponse} object.
	 * @param page     a {@link io.spotnext.itemtype.cms.model.CmsPage} object.
	 * @param e        a {@link java.lang.Exception} object.
	 */
//	protected void renderErrorPage(final HttpServletRequest request, final HttpServletResponse response,
//			final CmsPage page, final Exception e) {
//
//	}

	/**
	 * <p>
	 * getTemplateScript.
	 * </p>
	 *
	 * @param page a {@link io.spotnext.itemtype.cms.model.CmsPage} object.
	 * @return a {@link java.lang.String} object.
	 */
//	protected String getTemplateScript(final CmsPage page) {
//		String templateScript = "";
//
//		if (StringUtils.isNotBlank(page.getContent())) {
//			templateScript = page.getContent();
//		} else if (page.getTemplate() != null && StringUtils.isNotBlank(page.getTemplate().getContent())) {
//			templateScript = page.getTemplate().getContent();
//		}
//
//		return templateScript;
//	}
}
