package io.spotnext.core.management.support;

public enum HttpAuthorizationType {
	BASIC("Basic"), DIGEST("Digest"), NTLM("NTLM");

	private String code;

	private HttpAuthorizationType(final String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return code;
	}
}
