package at.spot.core.shell;

import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.shell.InputProvider;
import org.springframework.shell.ResultHandler;
import org.springframework.shell.Shell;

import at.spot.core.infrastructure.annotation.logging.Log;

//@Service
//@Order
public class SpotShellService extends Shell {

	public SpotShellService(final ResultHandler resultHandler) {
		super(resultHandler);
	}

	@PostConstruct
	@Log(message = "Starting up shell ...")
	@Override
	public void run(final InputProvider inputProvider) throws IOException {
		super.run(inputProvider);
	}
}
