package at.spot.mail;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import at.spot.core.CoreInit;

//@EnableCaching
//@EnableScheduling
//@EnableAsync
//@Service
public class MailInit extends CoreInit {
	public static void main(String[] args) throws Exception {
		new ClassPathXmlApplicationContext(new String[] { "spring-mail.xml" }).close();
	}
}
