package io.spotnext.sample;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.context.annotation.Import;

import io.spotnext.cms.CmsBaseConfiguration;
import io.spotnext.core.infrastructure.exception.ModuleInitializationException;
import io.spotnext.core.infrastructure.support.init.Bootstrap;
import io.spotnext.core.infrastructure.support.init.ModuleInit;

@Import(value = CmsBaseConfiguration.class)
@EnableAutoConfiguration(exclude = { ThymeleafAutoConfiguration.class })
@SpringBootApplication(scanBasePackages = { "io.spotnext.sample" })
public class SampleInit extends ModuleInit {

	@Override
	protected void initialize() throws ModuleInitializationException {
		//
	}

	@Override
	protected void importInitialData() throws ModuleInitializationException {
		super.importInitialData();
	}

	@Override
	protected void importSampleData() throws ModuleInitializationException {
		super.importSampleData();
	}

	public static void main(final String[] args) throws Exception {
		Bootstrap.bootstrap(SampleInit.class, new String[] { "io.spotnext.sample.types" }, args).run();
	}

}
