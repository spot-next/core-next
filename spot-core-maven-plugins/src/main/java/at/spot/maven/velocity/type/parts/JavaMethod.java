package at.spot.maven.velocity.type.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import at.spot.maven.velocity.MethodModifier;

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

	@Override
	public Set<String> getImports() {
		final Set<String> allImports = super.getImports();
		allImports.addAll(arguments.stream().flatMap(a -> a.getImports().stream()).collect(Collectors.toSet()));

		return allImports;
	}
}
