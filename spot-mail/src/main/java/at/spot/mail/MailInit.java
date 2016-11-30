package at.spot.mail;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.init.ModuleInit;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.support.util.PropertyUtil;

@Service
@EnableAsync
@EnableScheduling
@DependsOn("coreInit")
@Order(value = 1)
public class MailInit extends ModuleInit {

	@Autowired
	protected LoggingService loggingService;

	@Override
	public void injectBeanDefinition(final BeanDefinitionRegistry parentContext) {
		final BeanDefinitionReader reader = new XmlBeanDefinitionReader(parentContext);
		reader.loadBeanDefinitions("classpath:spring-mail.xml");
	}

	@Log(message = "Starting mail module ...")
	@Override
	public void initialize() {
		//
	}

	@Override
	public Properties getConfiguration() {
		return PropertyUtil.loadPropertiesFromClassPath("classpath:mail.properties");
	}

}
