package at.spot.spring.web.dto;

import com.google.gson.annotations.Expose;

public class Status {
	@Expose
	protected String code;
	@Expose
	protected String message;

	public Status(final String code, final String message) {
		this.code = code;
		this.message = message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(final String message) {
		this.message = message;
	}
}
