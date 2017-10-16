package at.spot.maven.velocity;

public class JavaMethodArgument extends AbstractJavaType {
	private static final long serialVersionUID = 1L;

	protected JavaMemberType type;

	public JavaMethodArgument(JavaMemberType type, String name) {
		this.name = name;
		this.type = type;
	}

	public JavaMemberType getType() {
		return type;
	}

	public void setType(JavaMemberType type) {
		this.type = type;
	}
}
