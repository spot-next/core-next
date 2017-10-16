package at.spot.maven.velocity;

public abstract class JavaMember extends AbstractJavaObject {
	private static final long serialVersionUID = 1L;

	protected JavaMemberType type;

	public JavaMemberType getType() {
		return type;
	}

	public void setType(JavaMemberType type) {
		this.type = type;
	}

}
