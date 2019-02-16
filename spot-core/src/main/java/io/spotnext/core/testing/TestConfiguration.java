package io.spotnext.core.testing;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfiguration {
	@Bean
	public TestMocker testMocker() {
		return new TestMocker();
	}
}
