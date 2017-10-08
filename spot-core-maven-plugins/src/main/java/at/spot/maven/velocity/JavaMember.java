package at.spot.maven.velocity;

public abstract class JavaMember extends AbstractJavaObject {
	private static final long serialVersionUID = 1L;

	protected Class<?> type;

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}
}
