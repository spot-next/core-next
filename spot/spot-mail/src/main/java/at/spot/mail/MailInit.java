package at.spot.mail;

import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import at.spot.core.CoreInit;
import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.support.init.ModuleConfig;

@Service
@EnableAsync
@EnableScheduling
@Order(value = 1)
@ModuleConfig(moduleName = "mail", modelPackagePaths = {
		"at.spot.mail.model" }, appConfigFile = "mail.properties", springConfigFile = "mail-spring.xml")
public class MailInit extends CoreInit {

	@Log(message = "Starting mail module ...")
	@Override
	public void initialize() {
		//
	}
}
