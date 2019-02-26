package io.spotnext.commerce;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.spotnext.commerce.facade.CartFacade;
import io.spotnext.commerce.facade.impl.DefaultCartFacade;
import io.spotnext.commerce.service.CartService;
import io.spotnext.commerce.service.impl.DefaultCartService;

/**
 * The base spring configuration for this spOt plugin. Can be imported with "@Import(value = CommerceConfiguration.class)".
 */
@Configuration
@ComponentScan(basePackages = { "io.spotnext.commerce.strategy", "io.spotnext.commerce.service",
		"io.spotnext.commerce.facade" })
@EnableAutoConfiguration
public class CommerceConfiguration {

	@Bean(name = { "defaultCartService", "cartService" })
	CartService cartService() {
		return new DefaultCartService();
	}

	@Bean(name = { "defaultCartFacade", "cartFacade" })
	CartFacade cartFacade() {
		return new DefaultCartFacade();
	}
}
