package at.spot.mail.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.service.ConfigurationService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.type.LogLevel;
import at.spot.core.management.exception.RemoteServiceInitException;
import at.spot.mail.service.ImapServiceEndpoint;

@Service
public class DefaultImapServiceEndpoint extends AbstractService implements ImapServiceEndpoint {

	private static final String CONFIG_PORT_KEY = "spot.service.imapservice.port";

	@Autowired
	protected ConfigurationService configurationService;

	@Log(logLevel = LogLevel.INFO, message = "Initiating IMAP service ...")
	@PostConstruct
	@Override
	public void init() throws RemoteServiceInitException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getPort() {
		return configurationService.getInteger(CONFIG_PORT_KEY);
	}

	@Override
	public InetAddress getBindAddress() {
		InetAddress ret = null;

		try {
			ret = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			loggingService.exception(e.getMessage(), e);
		}

		return ret;
	}

	@Override
	public void shutdown() throws RemoteServiceInitException {
		// TODO Auto-generated method stub

	}

}
