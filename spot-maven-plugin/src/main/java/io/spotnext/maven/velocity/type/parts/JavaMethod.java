package io.spotnext.maven.velocity.type.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import io.spotnext.maven.velocity.JavaMethodModifier;

/**
 * The method type both handles regular (static and instance) methods. But it also provides a way to add constructors, just don't add a return type.
 */
public class JavaMethod extends JavaMember {
	private static final long serialVersionUID = 1L;

	protected final Set<JavaMethodModifier> methodModifiers = new TreeSet<>();
	protected final List<JavaMethodArgument> arguments = new ArrayList<>();

	protected String codeBlock;

	/**
	 * <p>
	 * Getter for the field <code>codeBlock</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCodeBlock() {
		return codeBlock;
	}

	/**
	 * <p>
	 * Setter for the field <code>codeBlock</code>.
	 * </p>
	 *
	 * @param codeBlock a {@link java.lang.String} object.
	 */
	public void setCodeBlock(String codeBlock) {
		this.codeBlock = codeBlock;
	}

	/**
	 * <p>
	 * Getter for the field <code>methodModifiers</code>.
	 * </p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<JavaMethodModifier> getMethodModifiers() {
		return methodModifiers;
	}

	/**
	 * <p>
	 * addMethodModifier.
	 * </p>
	 *
	 * @param modifier a {@link io.spotnext.maven.velocity.JavaMethodModifier} object.
	 */
	public void addMethodModifier(JavaMethodModifier modifier) {
		this.methodModifiers.add(modifier);
	}

	/**
	 * <p>
	 * addArgument.
	 * </p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param type a {@link io.spotnext.maven.velocity.type.parts.JavaMemberType} object.
	 */
	public void addArgument(String name, JavaMemberType type) {
		this.arguments.add(new JavaMethodArgument(type, name));
	}

	/**
	 * <p>
	 * addArgument.
	 * </p>
	 *
	 * @param argument to add to the method.
	 */
	public void addArgument(JavaMethodArgument argument) {
		this.arguments.add(argument);
	}

	/**
	 * <p>
	 * Getter for the field <code>arguments</code>.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<JavaMethodArgument> getArguments() {
		return arguments;
	}

	/** {@inheritDoc} */
	@Override
	public Set<String> getImports() {
		final Set<String> allImports = super.getImports();
		allImports.addAll(arguments.stream().flatMap(a -> a.getImports().stream()).collect(Collectors.toSet()));

		return allImports;
	}
}
