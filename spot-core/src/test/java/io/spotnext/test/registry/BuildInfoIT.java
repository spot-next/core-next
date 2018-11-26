package io.spotnext.test.registry;

import static org.junit.Assert.assertNotNull;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.Test;

import io.spotnext.core.infrastructure.support.spring.BuildInfo;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.core.testing.AbstractIntegrationTest;

public class BuildInfoIT extends AbstractIntegrationTest {

	@Autowired
	BuildInfo buildInfo;

	@Override
	protected void prepareTest() {
	}

	@Override
	protected void teardownTest() {
	}

	@Test
	public void testGetBuildInfoFromRegistry() {
		final BuildInfo buildInfo = Registry.getBuildInfos();

		assertNotNull(buildInfo);
		assertNotNull(buildInfo.getBranch());
	}

	@Test
	public void testBuildInfoInjection() {
		assertNotNull(buildInfo);
		assertNotNull(buildInfo.getBranch());
	}
}
