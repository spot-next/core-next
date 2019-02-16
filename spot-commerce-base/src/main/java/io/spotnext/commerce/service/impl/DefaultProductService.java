package io.spotnext.commerce.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.commerce.service.CatalogService;
import io.spotnext.commerce.service.ProductService;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.core.persistence.query.JpqlQuery;
import io.spotnext.core.persistence.query.LambdaQuery;
import io.spotnext.core.persistence.query.QueryResult;
import io.spotnext.core.persistence.query.lambda.SerializablePredicate;
import io.spotnext.core.persistence.service.QueryService;
import io.spotnext.itemtype.commerce.catalog.Category;
import io.spotnext.itemtype.commerce.catalog.Product;
import io.spotnext.itemtype.commerce.catalog.VariantProduct;
import io.spotnext.itemtype.core.catalog.CatalogVersion;

@Service
public class DefaultProductService extends AbstractService implements ProductService {

	@Autowired
	protected QueryService queryService;

	@Autowired
	protected CatalogService catalogService;

	@Override
	public Optional<Product> getProductForId(String productId) {
//		final List<Product> products = findProducts(
//				p -> catalogService.getSessionCatalogVersions().contains(p.getCatalogVersion()) && p.getUid().equals(productId));

		final Set<CatalogVersion> cvs = catalogService.getSessionCatalogVersions();
		final List<Product> products = getProductForIdAndCatalogVersion(Product.class, Arrays.asList(productId), cvs);

		if (products.size() > 1) {
			throw new ModelNotUniqueException(String.format("Multiple products found for the uid=%", productId));
		}

		return products.stream().findFirst();
	}

	@Override
	public Optional<Product> getProductForId(String productId, CatalogVersion catalogVersion) {
//		final List<Product> products = findProducts(p -> p.getCatalogVersion().equals(catalogVersion) && p.getUid().equals(productId));

		return getProductForIdAndCatalogVersion(Product.class, Arrays.asList(productId), Arrays.asList(catalogVersion)).stream().findFirst();
	}

	protected <P extends Product> List<P> getProductForIdAndCatalogVersion(Class<P> productType, Collection<String> productIds,
			Collection<CatalogVersion> catalogVersion) {
		String cvQuery = "";

		if (CollectionUtils.isNotEmpty(catalogVersion)) {
			cvQuery = " AND catalogVersion IN :catalogVersions ";
		}

		String productIdQuery = "";

		if (CollectionUtils.isNotEmpty(productIds)) {
			productIdQuery = " AND uid IN :productIds ";
		}

		final JpqlQuery<P> query = new JpqlQuery<>(
				"SELECT p FROM " + productType.getName() + " AS p WHERE 1 = 1 " + productIdQuery + cvQuery,
				productType);

		if (CollectionUtils.isNotEmpty(productIds)) {
			query.addParam("productIds", productIds);
		}

		if (CollectionUtils.isNotEmpty(catalogVersion)) {
			query.addParam("catalogVersions", Arrays.asList(catalogVersion));
		}

		final QueryResult<P> result = queryService.query(query);

		return result.getResults();
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

//	@Override
//	public List<Product>> getProducts(Set<CatalogVersion> catalogVersions) {
//		
//	}

	@Override
	public Map<Product, List<VariantProduct>> getAllVariantProducts(Set<CatalogVersion> catalogVersions) {
		final List<VariantProduct> products = getProductForIdAndCatalogVersion(VariantProduct.class, null, catalogService.getSessionCatalogVersions());

		final List<String> variantsWithNoBaseProduct = products.stream() //
				.filter(v -> v.getBaseProduct() == null) //
				.map(v -> v.getUid()) //
				.collect(Collectors.toList());

		if (CollectionUtils.isNotEmpty(variantsWithNoBaseProduct)) {
			Logger.warn("Variant products without a base product found: " + StringUtils.join(variantsWithNoBaseProduct, ","));
		}

		return products.stream() //
				.filter(v -> v.getBaseProduct() != null) //
				.collect(Collectors.groupingBy(VariantProduct::getBaseProduct));
	}

	@Override
	public List<Product> getAllProducts(Set<CatalogVersion> catalogVersions) {
		return getProductForIdAndCatalogVersion(Product.class, null, catalogVersions);
	}

	protected List<Product> findProducts(SerializablePredicate<Product> filter) {
		final LambdaQuery<Product> query = new LambdaQuery<>(Product.class).filter(filter);
		final QueryResult<Product> result = queryService.query(query);
		return result.getResults();
	}
}
