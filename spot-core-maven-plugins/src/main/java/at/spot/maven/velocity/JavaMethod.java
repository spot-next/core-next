package at.spot.maven.velocity;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class JavaMethod extends JavaMember {
	private static final long serialVersionUID = 1L;

	protected final Set<MethodModifier> modifiers = new TreeSet<>();
	protected final List<JavaMethodArgument> arguments = new ArrayList<>();

	protected String codeBlock;

	public String getCodeBlock() {
		return codeBlock;
	}

	public void setCodeBlock(String codeBlock) {
		this.codeBlock = codeBlock;
	}

	public Set<MethodModifier> getModifiers() {
		return modifiers;
	}

	public void addArgument(String name, JavaMemberType type) {
		this.arguments.add(new JavaMethodArgument(type, name));
	}

	public List<JavaMethodArgument> getArguments() {
		return arguments;
	}
}
