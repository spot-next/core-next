package io.spotnext.commerce.service;

import java.util.List;

import io.spotnext.itemtype.core.catalog.Catalog;

/**
 * This service provides functionality around the {@link Catalog} type.
 */
public interface CatalogService {
	/**
	 * Copies all content from the source catalog to the target catalog.
	 * 
	 * @param source
	 * @param target
	 * @param full
	 *            also copies already existing content
	 */
	void synchronizeCatalog(Catalog source, Catalog target, boolean full);

	/**
	 * Returns the current active session catalogs.
	 * 
	 */
	List<Catalog> getSessionCatalogs();

	/**
	 * Returns the catalog with the given id or null.
	 * 
	 * @param id
	 */
	Catalog getCatalog(String id);

	/**
	 * Adds a catalog to the current session.
	 * 
	 * @param catalog
	 */
	void addSessionCatalogs(Catalog catalog);

	/**
	 * Removes the catalog with the given id fro the current session.
	 * 
	 * @param catalogId
	 */
	void removeSessionCatalogs(String catalogId);
}
