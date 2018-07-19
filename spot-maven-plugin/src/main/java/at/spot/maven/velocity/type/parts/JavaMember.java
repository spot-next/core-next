package at.spot.maven.velocity.type.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import at.spot.maven.velocity.JavaMemberModifier;
import at.spot.maven.velocity.type.AbstractJavaObject;

public abstract class JavaMember extends AbstractJavaObject {
	private static final long serialVersionUID = 1L;

	protected JavaMemberType type;
	protected final List<JavaMemberModifier> modifiers = new ArrayList<>();

	public JavaMemberType getType() {
		return type;
	}

	public void setType(JavaMemberType type) {
		this.type = type;
	}

	@Override
	public Set<String> getImports() {
		final Set<String> allImports = super.getImports();

		if (type != null) {
			allImports.addAll(type.getImports());
		}

		return allImports;
	}

	public void addModifier(JavaMemberModifier modifier) {
		this.modifiers.add(modifier);
	}

	public List<JavaMemberModifier> getModifiers() {
		return modifiers;
	}

}
