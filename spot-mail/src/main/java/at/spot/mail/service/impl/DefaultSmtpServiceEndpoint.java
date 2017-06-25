package at.spot.mail.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.subethamail.smtp.AuthenticationHandler;
import org.subethamail.smtp.AuthenticationHandlerFactory;
import org.subethamail.smtp.TooMuchDataException;
import org.subethamail.smtp.helper.SimpleMessageListener;
import org.subethamail.smtp.helper.SimpleMessageListenerAdapter;
import org.subethamail.smtp.server.SMTPServer;

import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.ModelValidationException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.support.LogLevel;
import at.spot.core.management.exception.RemoteServiceInitException;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.mail.model.Mail;
import at.spot.mail.service.SmtpServiceEndpoint;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Implements a simple SMTP service endpoint. Received mails are stored as
 * {@link Mail} objects in the given MailBox
 *
 */
@Service
public class DefaultSmtpServiceEndpoint extends AbstractService implements SmtpServiceEndpoint, SimpleMessageListener {

	protected static final String CONFIG_KEY_PORT = "service.mail.smtp.port";
	protected static final String CONFIG_KEY_BIND_ADDRESS = "service.mail.smtp.bindaddress";
	protected static final int DEFAULT_PORT = 8025;
	protected static final String DEFAULT_BIND_ADDRESS = "localhost";

	protected boolean stop = false;

	protected final SMTPServer smtpServer = new SMTPServer(new SimpleMessageListenerAdapter(this),
			new SMTPAuthHandlerFactory());

	protected Queue<Mail> mailQueue = new ConcurrentLinkedDeque<>();

	@Autowired
	protected ModelService modelService;

	@Log(logLevel = LogLevel.INFO, message = "Initiating SMTP service ...")
	@PostConstruct
	@Override
	public void init() throws RemoteServiceInitException {
		this.smtpServer.setBindAddress(getBindAddress());
		this.smtpServer.setPort(getPort());

		try {
			this.smtpServer.start();
		} catch (final RuntimeException e) {
			loggingService.exception("Cannot start SMTP server: " + e.getMessage(), e);
		}
	}

	@SuppressFBWarnings("IL_INFINITE_LOOP")
	protected void runMessageQueueLoop() {
		Mail mail = null;

		while (!stop) {
			mail = mailQueue.poll();

			if (mail != null) {
				try {
					modelService.save(mail);
				} catch (ModelSaveException | ModelNotUniqueException | ModelValidationException e) {
					loggingService.exception("Can't save received mail.", e);
				}
			}
		}
	}

	@Override
	public int getPort() {
		return configurationService.getInteger(CONFIG_KEY_PORT, DEFAULT_PORT);
	}

	@Override
	public InetAddress getBindAddress() {
		InetAddress ret = null;

		try {
			final String address = configurationService.getString(CONFIG_KEY_BIND_ADDRESS, DEFAULT_BIND_ADDRESS);
			ret = InetAddress.getByName(address);
		} catch (final UnknownHostException e) {
			loggingService.exception(e.getMessage(), e);
		}

		return ret;
	}

	@Override
	public boolean accept(final String paramString1, final String paramString2) {
		return true;
	}

	@Override
	public void deliver(final String from, final String recipient, final InputStream data)
			throws TooMuchDataException, IOException {

		saveMail(from, recipient, data);
	}

	protected synchronized void saveMail(final String from, final String recipient, final InputStream data)
			throws IOException {
		final Mail mail = new Mail();

		mail.sender = from;
		mail.toRecipients.add(recipient);
		mail.content = IOUtils.toString(data, "UTF-8");

		try {
			modelService.save(mail);
		} catch (ModelSaveException | ModelNotUniqueException | ModelValidationException e) {
			loggingService.exception("Can't save received mail.", e);
		}
	}

	final static class SMTPAuthHandlerFactory implements AuthenticationHandlerFactory {
		private static final String LOGIN_MECHANISM = "LOGIN";

		@Override
		public AuthenticationHandler create() {
			return new SMTPAuthHandler();
		}

		@Override
		public List<String> getAuthenticationMechanisms() {
			final List<String> result = new ArrayList<>();
			result.add("LOGIN");
			return result;
		}
	}

	final static class SMTPAuthHandler implements AuthenticationHandler {
		private static final String USER_IDENTITY = "User";
		private static final String PROMPT_USERNAME = "334 VXNlcm5hbWU6";
		private static final String PROMPT_PASSWORD = "334 UGFzc3dvcmQ6";
		private int pass;

		SMTPAuthHandler() {
			this.pass = 0;
		}

		@Override
		public String auth(final String clientInput) {
			String prompt;

			if (++this.pass == 1) {
				prompt = "334 VXNlcm5hbWU6";
			} else {
				if (this.pass == 2) {
					prompt = "334 UGFzc3dvcmQ6";
				} else {
					this.pass = 0;
					prompt = null;
				}
			}
			return prompt;
		}

		@Override
		public Object getIdentity() {
			return "User";
		}
	}

	@Override
	public void shutdown() throws RemoteServiceInitException {
		smtpServer.stop();
	}
}
