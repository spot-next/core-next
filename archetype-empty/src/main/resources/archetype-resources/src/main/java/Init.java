package $package;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.DependsOn;

import io.spotnext.core.CoreInit;
import io.spotnext.core.infrastructure.exception.ModuleInitializationException;
import io.spotnext.core.infrastructure.support.init.ModuleInit;

@DependsOn(value = "coreInit")
@SpringBootApplication(scanBasePackages = { "$package" })
public class Init extends ModuleInit {

	@Override
	protected void initialize() throws ModuleInitializationException {
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
		ModuleInit.bootstrap(CoreInit.class, Init.class, args);
	}

}
