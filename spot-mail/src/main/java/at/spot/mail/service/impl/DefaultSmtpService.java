package at.spot.mail.service.impl;

import java.net.SocketException;

import at.spot.core.management.exception.RemoteServiceInitException;
import at.spot.mail.service.SmtpService;

public class DefaultSmtpService implements SmtpService {

	@Override
	public void init() throws RemoteServiceInitException, SocketException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

}
