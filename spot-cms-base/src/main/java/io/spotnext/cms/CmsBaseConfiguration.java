package io.spotnext.cms;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = { "io.spotnext.cms.strategy", "io.spotnext.cms.service",
		"io.spotnext.cms.rendering.transformers" })
@EnableAutoConfiguration
public class CmsBaseConfiguration {
	//
}
