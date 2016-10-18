package at.spot.mail.service.impl;

import java.net.SocketException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.type.LogLevel;
import at.spot.core.management.exception.RemoteServiceInitException;
import at.spot.mail.service.ImapService;

@Service
public class DefaultImapService extends AbstractService implements ImapService {

	private static final String CONFIG_PORT_KEY = "spot.service.remotetypeservice.port";

	@Log(logLevel = LogLevel.INFO, message = "Initiating imap service ...")
	@PostConstruct
	@Override
	public void init() throws RemoteServiceInitException, SocketException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getPort() {
		// return configurationService.getInteger(CONFIG_PORT_KEY);
		return 0;
	}

}
