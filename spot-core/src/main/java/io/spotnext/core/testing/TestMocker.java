package io.spotnext.core.testing;

import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.persistence.service.PersistenceService;
import io.spotnext.core.persistence.service.TransactionService;
import io.spotnext.itemtype.core.catalog.Catalog;
import io.spotnext.itemtype.core.catalog.CatalogVersion;

@Service
public class TestMocker {

	@Autowired
	protected PersistenceService persistenceService;

	@Autowired
	protected TransactionService transactionService;

	@Autowired
	protected ModelService modelService;

	public Catalog mockCatalog() {
		final Catalog catalog = modelService.create(Catalog.class);
		catalog.setUid("testCatalog-" + System.currentTimeMillis());

		final CatalogVersion catalogVersionOnline = modelService.create(CatalogVersion.class);
		catalogVersionOnline.setCatalog(catalog);
		catalogVersionOnline.setUid("Online");

		final CatalogVersion catalogVersionStaged = modelService.create(CatalogVersion.class);
		catalogVersionStaged.setCatalog(catalog);
		catalogVersionStaged.setUid("Staged");
		catalogVersionStaged.setSynchronizationTarget(catalogVersionOnline);

		modelService.save(catalogVersionOnline);
		modelService.save(catalogVersionStaged);
		modelService.refresh(catalog);

		Assertions.assertEquals(2, catalog.getVersions().size());

		return catalog;
	}
}
