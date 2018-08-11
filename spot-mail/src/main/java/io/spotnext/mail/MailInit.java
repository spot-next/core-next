package io.spotnext.mail;

import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import io.spotnext.core.CoreInit;
import io.spotnext.core.infrastructure.annotation.logging.Log;
import io.spotnext.core.infrastructure.support.init.ModuleConfig;

@Service
@EnableAsync
@EnableScheduling
@Order(value = 1)
@ModuleConfig(moduleName = "mail", modelPackagePaths = {
		"io.spotnext.mail.model" }, appConfigFile = "mail.properties", springConfigFile = "mail-spring.xml")
public class MailInit extends CoreInit {

	@Log(message = "Starting mail module ...")
	public void initialize() {
		//
	}
}
