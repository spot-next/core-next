package io.spotnext.sample.service;

import javax.annotation.PostConstruct;

import org.simplejavamail.email.Email;
import org.simplejavamail.mailer.Mailer;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.impl.AbstractService;

@Service
public class EmailService extends AbstractService {

	@Value("${mail.smtp.host}")
	private String mailHost;

	@Value("${mail.smtp.port}")
	private Integer mailPort;

	@Value("${mail.smtp.user}")
	private String mailUser;

	@Value("${mail.smtp.password}")
	private String mailPassword;

	private Mailer mailer;

	@PostConstruct
	public void init() {
		mailer = MailerBuilder.withSMTPServer(mailHost, mailPort, null, null).buildMailer();
	}

	public void send(final Email email) {
		mailer.sendMail(email);
	}
}
