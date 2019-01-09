package io.spotnext.commerce;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The base spring configuration for this spOt plugin. Can be imported with "@Import(value = CommerceConfiguration.class)".
 */
@Configuration
@ComponentScan(basePackages = { "io.spotnext.commerce.strategy", "io.spotnext.commerce.service",
		"io.spotnext.commerce.facade" })
@EnableAutoConfiguration
public class CommerceConfiguration {
	//
}
