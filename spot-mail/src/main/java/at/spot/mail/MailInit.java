package at.spot.mail;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import at.spot.core.CoreInit;

@Service
public class MailInit extends CoreInit {
	public static void main(String[] args) throws Exception {
		new ClassPathXmlApplicationContext("spring-mail.xml").close();
	}
}
