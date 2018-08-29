package io.spotnext.core.management.support;

/**
 * <p>HttpAuthorizationType class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public enum HttpAuthorizationType {
	BASIC("Basic"), DIGEST("Digest"), NTLM("NTLM");

	private String code;

	private HttpAuthorizationType(final String code) {
		this.code = code;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return code;
	}
}
