package io.spotnext.commerce.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.commerce.service.CatalogService;
import io.spotnext.core.infrastructure.http.Session;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.infrastructure.service.SessionService;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.itemtype.core.catalog.Catalog;
import io.spotnext.itemtype.core.catalog.CatalogVersion;

public class DefaultCatalogService extends AbstractService implements CatalogService {

	protected static final String PARAM_SESSION_CATALOGVERSIONS = "sessionCatalogVersions";

	@Autowired
	protected QueryService queryService;

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected SessionService sessionService;

	@Override
	public void synchronizeCatalog(Catalog source, Catalog target, boolean full) {
		//
	}

	@Override
	public Catalog getCatalog(String id) {
		return modelService.get(new ModelQuery<>(Catalog.class, Collections.singletonMap(Catalog.PROPERTY_UID, id)));
	}

	@Override
	public Set<CatalogVersion> getSessionCatalogVersions() {
		return getSessionCatalogVersions(() -> Collections.emptySet());
	}

	@Override
	public void addSessionCatalogVersion(CatalogVersion catalogVersion) {
		final Set<CatalogVersion> catalogVersions = getSessionCatalogVersions(() -> new HashSet<>());
		catalogVersions.add(catalogVersion);
		sessionService.getCurrentSession().setAttribute(PARAM_SESSION_CATALOGVERSIONS, catalogVersions);
	}

	@Override
	public void removeSessionCatalogVersion(String catalogId) {
		sessionService.getCurrentSession().setAttribute(PARAM_SESSION_CATALOGVERSIONS, null);
	}

	protected Set<CatalogVersion> getSessionCatalogVersions(Supplier<Set<CatalogVersion>> nullFallback) {
		final Session session = sessionService.getCurrentSession();

		final Set<CatalogVersion> catalogVersions = session.<Set<CatalogVersion>>attribute(PARAM_SESSION_CATALOGVERSIONS) //
				.orElse(nullFallback != null ? nullFallback.get() : null);

		return catalogVersions;
	}

}
