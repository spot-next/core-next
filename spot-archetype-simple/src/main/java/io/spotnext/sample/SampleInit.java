package io.spotnext.sample;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.spotnext.core.CoreInit;
import io.spotnext.core.infrastructure.exception.ModuleInitializationException;
import io.spotnext.core.infrastructure.support.init.Bootstrap;

@SpringBootApplication(scanBasePackages = { "io.spotnext.sample" })
public class SampleInit extends CoreInit {

	@Override
	protected void initialize() throws ModuleInitializationException {
		super.initialize();
	}

	@Override
	protected void importInitialData() throws ModuleInitializationException {
		super.importInitialData();
	}

	@Override
	protected void importSampleData() throws ModuleInitializationException {
		super.importSampleData();
	}

	public static void main(final String[] args) {
		Bootstrap.bootstrap(SampleInit.class, new String[] { "io.spotnext.sample" }).run();
	}

}
