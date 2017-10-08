package at.spot.maven.velocity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class JavaMethod extends JavaMember {
	private static final long serialVersionUID = 1L;

	protected final Set<MethodModifier> modifiers = new TreeSet<>();
	protected final Map<String, Class<?>> arguments = new HashMap<>();

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

	public Map<String, Class<?>> getArguments() {
		return arguments;
	}

	public void addArgument(String name, Class<?> type) {
		this.arguments.put(name, type);
	}
}
