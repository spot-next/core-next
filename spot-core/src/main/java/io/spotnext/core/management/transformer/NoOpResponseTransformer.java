package io.spotnext.core.management.transformer;

import org.springframework.stereotype.Service;

/**
 * Does not do anything.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class NoOpResponseTransformer implements ResponseTransformer {

	@Override
	public String render(final Object arg) throws Exception {
		return null;
	}

	@Override
	public String handleResponse(Object responseObject) throws Exception {
		return null;
	}
}
