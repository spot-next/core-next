package io.spotnext.sample.service;

import org.simplejavamail.email.Email;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.infrastructure.support.spring.PostConstructor;

@Service
//@SuppressFBWarnings("UUF_UNUSED_FIELD")
public class EmailService extends AbstractService implements PostConstructor
{

	@Value("${mail.smtp.host}")
	private String mailHost;

	@Value("${mail.smtp.port}")
	private Integer mailPort;

	@Value("${mail.smtp.user}")
	private String mailUser;

	@Value("${mail.smtp.password}")
	private String mailPassword;

	private Mailer mailer;

	@Override
	public void setup() {
		mailer = MailerBuilder.withSMTPServer(mailHost, mailPort, null, null).buildMailer();
	}

	public void send(final Email email) {
		mailer.sendMail(email);
	}
}
