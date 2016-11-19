package at.spot.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.init.ModuleInit;
import at.spot.core.infrastructure.service.LoggingService;

@Service
@EnableAsync
@EnableScheduling
@DependsOn("coreInit")
public class MailInit extends ModuleInit {

	@Autowired
	protected LoggingService loggingService;

	@Override
	public void injectBeanDefinition(BeanDefinitionRegistry parentContext) {
		BeanDefinitionReader reader = new XmlBeanDefinitionReader(parentContext);
		reader.loadBeanDefinitions("classpath:spring-mail.xml");
	}

	@Log(message = "Starting mail module ...")
	@Override
	public void initialize() {
		//
	}

}
