package at.spot.test.registry;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Resource;

import org.junit.Test;

import at.spot.core.infrastructure.support.spring.BuildInfo;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.core.testing.AbstractIntegrationTest;

public class BuildInfoIT extends AbstractIntegrationTest {

	@Resource
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
