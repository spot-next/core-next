package at.spot.core.shell;

import javax.annotation.PostConstruct;

import org.springframework.shell.Bootstrap;
import org.springframework.shell.core.ExitShellRequest;

import at.spot.core.infrastructure.annotation.logging.Log;

//@Service
//@Order
public class SpotShellService extends Bootstrap {

	@PostConstruct
	@Log(message = "Starting up shell ...")
	@Override
	public ExitShellRequest run() {
		return super.run();
	}
}
