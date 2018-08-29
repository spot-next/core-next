package io.spotnext.core.infrastructure.support;

/**
 * <p>MimeType class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public enum MimeType {
	JAVASCRIPT("application/javascript"), JSON("application/json"), PLAINTEXT("text/plain"), XHTML("application/xhtml+xml"), HTML("text/html");

	private String mimeType;

	private MimeType(final String mimeType) {
		this.mimeType = mimeType;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.mimeType;
	}
}
