package io.spotnext.core.management.transformer;

import org.springframework.stereotype.Service;

import spark.ResponseTransformer;

/**
 * <p>PlainTextResponseTransformer class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class PlainTextResponseTransformer implements ResponseTransformer {

	/** {@inheritDoc} */
	@Override
	public String render(final Object arg) throws Exception {
		return arg.toString();
	}
}
