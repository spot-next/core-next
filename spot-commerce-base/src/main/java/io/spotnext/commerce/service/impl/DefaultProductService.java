package io.spotnext.commerce.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.commerce.service.CatalogService;
import io.spotnext.commerce.service.ProductService;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.core.persistence.query.LambdaQuery;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.query.lambda.SerializablePredicate;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.itemtype.commerce.catalog.Category;
import io.spotnext.itemtype.commerce.catalog.Product;
import io.spotnext.itemtype.core.catalog.CatalogVersion;

@Service
public class DefaultProductService extends AbstractService implements ProductService {

	@Autowired
	protected QueryService queryService;

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected CatalogService catalogService;

	@Override
	public Optional<Product> getProductForId(String productId) {
		final List<Product> products = findProducts(p -> catalogService.getSessionCatalogVersions().contains(p.getCatalogVersion()) && p.getUid().equals(productId));
		
		if (products.size() > 1) {
			throw new ModelNotUniqueException(String.format("Multiple products found for the uid=%", productId));
		}
		
		return products.stream().findFirst();
	}

	@Override
	public Optional<Product> getProductForId(String productId, CatalogVersion catalogVersion) {
		final List<Product> products = findProducts(p -> p.getCatalogVersion().equals(catalogVersion) && p.getUid().equals(productId));

		return products.stream().findFirst();
	}

	@Override
	public List<Product> getProductsInCategory(Category category) {

//		return findProducts(p -> catalogService.getSessionCatalogVersions().contains(p.getCatalogVersion()) && p.getCategories().contains(category));
		return Collections.emptyList();
	}

	@Override
	public List<Product> getAllProducts() {
		return getAllProducts(catalogService.getSessionCatalogVersions());
	}

	@Override
	public List<Product> getAllProducts(Set<CatalogVersion> catalogVersions) {
		return findProducts(p -> catalogVersions.contains(p.getCatalogVersion()));
	}

	protected List<Product> findProducts(SerializablePredicate<Product> filter) {
		final LambdaQuery<Product> query = new LambdaQuery<>(Product.class).filter(filter);
		final QueryResult<Product> result = queryService.query(query);
		return result.getResults();
	}
}
