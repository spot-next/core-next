package io.spotnext.core.infrastructure.support.spring;

import org.springframework.beans.factory.InitializingBean;

public interface PostConstructor extends InitializingBean {

	@Override
	default void afterPropertiesSet() throws Exception {
		setup();
	}

	/**
	 * Called by Spring after object initialization.
	 */
	void setup();
}
