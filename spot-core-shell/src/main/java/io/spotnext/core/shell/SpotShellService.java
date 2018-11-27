package io.spotnext.core.shell;

import java.io.IOException;

import org.springframework.shell.InputProvider;
import org.springframework.shell.ResultHandler;
import org.springframework.shell.Shell;

import io.spotnext.core.infrastructure.annotation.logging.Log;
import io.spotnext.core.infrastructure.support.spring.PostConstructor;

//@Service
//@Order
public class SpotShellService extends Shell {

	public SpotShellService(final ResultHandler resultHandler) {
		super(resultHandler);
	}

	@Log(message = "Starting up shell ...")
	@Override
	public void run(final InputProvider inputProvider) throws IOException {
		super.run(inputProvider);
	}
}
