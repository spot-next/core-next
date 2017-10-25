package at.spot.maven.velocity.type.parts;

import java.util.Set;

import at.spot.maven.velocity.type.AbstractObject;

public class JavaMethodArgument extends AbstractObject {
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

	@Override
	public Set<String> getImports() {
		final Set<String> allImports = super.getImports();
		allImports.addAll(type.getImports());

		return allImports;
	}
}
