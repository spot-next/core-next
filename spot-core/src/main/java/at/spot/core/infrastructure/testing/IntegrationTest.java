package at.spot.core.infrastructure.testing;

import org.junit.BeforeClass;

import at.spot.core.infrastructure.support.init.ModuleInit;

/**
 * The base class for all integration tests.
 */
public abstract class IntegrationTest {
	protected abstract <M extends ModuleInit> Class<M> getModuleInit();

	/**
	 * Bootstrap spot.
	 */
	@BeforeClass
	protected void bootstrap() {

	}
}
