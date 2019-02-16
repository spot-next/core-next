package io.spotnext.commerce.service;

import java.util.Set;

import io.spotnext.itemtype.core.catalog.Catalog;
import io.spotnext.itemtype.core.catalog.CatalogVersion;

/**
 * This service provides functionality around the {@link io.spotnext.itemtype.core.catalog.Catalog} type.
 */
public interface CatalogService {
	/**
	 * Copies all content from the source catalog to the target catalog.
	 *
	 * @param source a {@link io.spotnext.itemtype.core.catalog.Catalog} object.
	 * @param target a {@link io.spotnext.itemtype.core.catalog.Catalog} object.
	 * @param full   also copies already existing content
	 */
	void synchronizeCatalog(Catalog source, Catalog target, boolean full);

	/**
	 * Returns the catalog with the given id or null.
	 *
	 * @param id a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.itemtype.core.catalog.Catalog} object.
	 */
	Catalog getCatalog(String id);

	/**
	 * Returns the current active session catalogs.
	 *
	 * @return a not-null set of all catalog versions registered with the current session.
	 */
	Set<CatalogVersion> getSessionCatalogVersions();

	/**
	 * Adds a catalog version to the current session.
	 *
	 * @param catalogVersion to be added to the current session
	 */
	void addSessionCatalogVersion(CatalogVersion catalogVersion);

	/**
	 * Removes the catalog version with the given id from the current session.
	 *
	 * @param catalogVersionId of the catalog version to remove
	 */
	void removeSessionCatalogVersion(String catalogVersionId);
}
