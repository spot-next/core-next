package io.spotnext.test.persistence;

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.Ignore;
import org.junit.Test;

import io.spotnext.core.persistence.hibernate.impl.HibernatePersistenceService;
import io.spotnext.core.testing.AbstractIntegrationTest;
import io.spotnext.itemtype.core.internationalization.Country;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

public class PersistenceCachingIT extends AbstractIntegrationTest {

	@Autowired
	HibernatePersistenceService persistenceService;
	
	Cache itemCache;
	
	@Override
	protected void prepareTest() {
		itemCache = CacheManager.ALL_CACHE_MANAGERS.get(0).getCache("items");
		itemCache.removeAll();
	}

	@Override
	protected void teardownTest() {
	}

	@Ignore
	@Test
	public void testSingleItemLoad() {
		persistenceService.getSession().clear();
		
		Country austria = modelService.get(Country.class, Collections.singletonMap("isoCode", "AT"));
		
		long fetchCount = persistenceService.getStatistics().getEntityFetchCount();
		persistenceService.getSession().evict(austria);
		
		modelService.get(Country.class, austria.getPk());
		
		long cacheHit = persistenceService.getStatistics().getSecondLevelCacheHitCount();
		
		assertEquals(1, itemCache.getSize());
		
	}
}
