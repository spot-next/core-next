package at.spot.core.integrationtest;

import at.spot.core.CoreInit;
import at.spot.core.infrastructure.support.init.ModuleInit;
import at.spot.core.infrastructure.testing.IntegrationTest;

public class PersistenceIntegrationTest extends IntegrationTest {

	@Override
	protected <M extends ModuleInit> Class<M> getModuleInit() {
		return (Class<M>) CoreInit.class;
	}

}
