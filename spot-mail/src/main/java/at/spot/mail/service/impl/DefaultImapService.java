package at.spot.mail.service.impl;

import java.net.SocketException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.management.exception.RemoteServiceInitException;
import at.spot.mail.service.ImapService;

@Service
public class DefaultImapService extends AbstractService implements ImapService {

	@PostConstruct
	@Override
	public void init() throws RemoteServiceInitException, SocketException {
		// TODO Auto-generated method stub

	}

	@Override
	public int getPort() {
		return 0;
	}

}
