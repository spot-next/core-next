package io.spotnext.core.infrastructure.support.spring;

import org.springframework.beans.factory.InitializingBean;

public interface PostConstructor extends InitializingBean {

	/**
	 * This is the default spring method, that just calls the "renamed" init method.
	 */
	@Override
	default void afterPropertiesSet() throws Exception {
		setup();
	}

	/**
	 * Called by Spring after object initialization.
	 */
	void setup();
}
