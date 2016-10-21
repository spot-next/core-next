package at.spot.mail.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import com.nilhcem.fakesmtp.core.exception.BindPortException;
import com.nilhcem.fakesmtp.core.exception.OutOfRangePortException;
import com.nilhcem.fakesmtp.server.SMTPServerHandler;

import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.type.LogLevel;
import at.spot.core.management.exception.RemoteServiceInitException;
import at.spot.mail.service.SmtpService;

@Component
public class DefaultSmtpService extends AbstractService implements SmtpService {

	protected SMTPServerHandler handler;

	@Log(logLevel = LogLevel.INFO, message = "Initiating SMTP service ...")
	@PostConstruct
	@Override
	public void init() throws RemoteServiceInitException {
		handler = SMTPServerHandler.INSTANCE;
		try {
			handler.startServer(getPort(), getBindAddress());
		} catch (BindPortException | OutOfRangePortException e) {
			throw new RemoteServiceInitException("Cannot start smtp service", e);
		}
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return 0;
	}

	public InetAddress getBindAddress() {
		InetAddress ret = null;

		try {
			ret = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			loggingService.exception(e.getMessage());
		}

		return ret;
	}
}
