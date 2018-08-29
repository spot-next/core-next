package io.spotnext.cms;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The base spring configuration for this spOt plugin. Can be imported with
 * "@Import(value = CmsBaseConfiguration.class)".
 */
@Configuration
@ComponentScan(basePackages = { "io.spotnext.cms.strategy", "io.spotnext.cms.service",
		"io.spotnext.cms.rendering.transformers" })
@EnableAutoConfiguration
public class CmsBaseConfiguration {
	//
}
