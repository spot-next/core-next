package at.spot.mail.type;

public enum UriType {
	FILE("file"), MAIL("mailto"), TEL("tel");

	private String code;

	private UriType(String code) {
		this.code = code;
	}
}
