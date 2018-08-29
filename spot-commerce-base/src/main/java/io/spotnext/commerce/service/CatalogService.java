package io.spotnext.commerce.service;

import java.util.List;

import io.spotnext.itemtype.core.catalog.Catalog;

/**
 * This service provides functionality around the {@link io.spotnext.itemtype.core.catalog.Catalog} type.
 */
public interface CatalogService {
	/**
	 * Copies all content from the source catalog to the target catalog.
	 *
	 * @param source a {@link io.spotnext.itemtype.core.catalog.Catalog} object.
	 * @param target a {@link io.spotnext.itemtype.core.catalog.Catalog} object.
	 * @param full
	 *            also copies already existing content
	 */
	void synchronizeCatalog(Catalog source, Catalog target, boolean full);

	/**
	 * Returns the current active session catalogs.
	 *
	 * @return a {@link java.util.List} object.
	 */
	List<Catalog> getSessionCatalogs();

	/**
	 * Returns the catalog with the given id or null.
	 *
	 * @param id a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.itemtype.core.catalog.Catalog} object.
	 */
	Catalog getCatalog(String id);

	/**
	 * Adds a catalog to the current session.
	 *
	 * @param catalog a {@link io.spotnext.itemtype.core.catalog.Catalog} object.
	 */
	void addSessionCatalogs(Catalog catalog);

	/**
	 * Removes the catalog with the given id fro the current session.
	 *
	 * @param catalogId a {@link java.lang.String} object.
	 */
	void removeSessionCatalogs(String catalogId);
}
