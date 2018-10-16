package io.spotnext.core.infrastructure.support;

public enum HttpHeader {
	ETag("ETag"),
	LastModified("Last-Modified"),
	IfModifiedSince("If-Modified-Since"),
	IfNoneMatch("If-None-Match");

	private String code;

	private HttpHeader(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return this.code;
	}
}
