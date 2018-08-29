package io.spotnext.maven.velocity.type;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import io.spotnext.maven.velocity.type.annotation.JavaAnnotation;
import io.spotnext.maven.velocity.type.base.JavaInterface;
import io.spotnext.maven.velocity.type.parts.JavaGenericTypeArgument;
import io.spotnext.maven.velocity.type.parts.JavaMethod;
import io.spotnext.maven.velocity.type.parts.JavaMethodArgument;

/**
 * <p>Abstract AbstractComplexJavaType class.</p>
 *
 * @since 1.0
 */
public abstract class AbstractComplexJavaType extends AbstractJavaObject {
	private static final long serialVersionUID = 1L;

	protected final Set<JavaInterface> interfaces = new HashSet<>();
	protected final Set<JavaMethod> methods = new HashSet<>();
	protected final Set<JavaGenericTypeArgument> genericArguments = new HashSet<>();

	protected String packagePath;
	protected JavaInterface superClass;

	/**
	 * <p>Constructor for AbstractComplexJavaType.</p>
	 */
	public AbstractComplexJavaType() {
	}

	/**
	 * <p>Constructor for AbstractComplexJavaType.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param packagePath a {@link java.lang.String} object.
	 */
	public AbstractComplexJavaType(String name, String packagePath) {
		setName(name);
		setPackagePath(packagePath);
	}

	/**
	 * <p>Constructor for AbstractComplexJavaType.</p>
	 *
	 * @param clazz a {@link java.lang.Class} object.
	 */
	public AbstractComplexJavaType(Class<?> clazz) {
		this(clazz.getSimpleName(), clazz.getPackage().getName());
	}

	/**
	 * <p>Getter for the field <code>packagePath</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getPackagePath() {
		return packagePath;
	}

	/**
	 * <p>Setter for the field <code>packagePath</code>.</p>
	 *
	 * @param packagePath a {@link java.lang.String} object.
	 */
	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}

	/**
	 * <p>Getter for the field <code>superClass</code>.</p>
	 *
	 * @return a {@link io.spotnext.maven.velocity.type.AbstractComplexJavaType} object.
	 */
	public AbstractComplexJavaType getSuperClass() {
		return superClass;
	}

	/**
	 * <p>Setter for the field <code>superClass</code>.</p>
	 *
	 * @param superClass a {@link io.spotnext.maven.velocity.type.base.JavaInterface} object.
	 */
	public void setSuperClass(JavaInterface superClass) {
		this.superClass = superClass;
		this.imports.add(superClass.getFullyQualifiedName());
	}

	/**
	 * <p>Setter for the field <code>superClass</code>.</p>
	 *
	 * @param superClass a {@link java.lang.Class} object.
	 */
	public void setSuperClass(Class<?> superClass) {
		setSuperClass(new JavaInterface(superClass.getPackage().getName(), superClass.getSimpleName()));
	}

	/**
	 * <p>Getter for the field <code>interfaces</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<JavaInterface> getInterfaces() {
		return Collections.unmodifiableSet(interfaces);
	}

	/**
	 * <p>Getter for the field <code>methods</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<JavaMethod> getMethods() {
		return Collections.unmodifiableSet(methods);
	}

	/**
	 * <p>addInterface.</p>
	 *
	 * @param iface a {@link io.spotnext.maven.velocity.type.base.JavaInterface} object.
	 */
	public void addInterface(JavaInterface iface) {
		this.interfaces.add(iface);
		this.imports.add(iface.getFullyQualifiedName());
	}

	/**
	 * <p>addMethod.</p>
	 *
	 * @param method a {@link io.spotnext.maven.velocity.type.parts.JavaMethod} object.
	 */
	public void addMethod(JavaMethod method) {
		this.methods.add(method);

		for (JavaMethodArgument arg : method.getArguments()) {
			if (arg.getType().isComplexType()) {
				this.imports.add(arg.getType().getFullyQualifiedName());
			}
		}

		if (method.getType().isComplexType()) {
			this.imports.add(method.getType().getFullyQualifiedName());
		}
	}

	/**
	 * <p>Getter for the field <code>genericArguments</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<JavaGenericTypeArgument> getGenericArguments() {
		return genericArguments;
	}

	/**
	 * <p>addGenericArgument.</p>
	 *
	 * @param argument a {@link io.spotnext.maven.velocity.type.parts.JavaGenericTypeArgument} object.
	 */
	public void addGenericArgument(final JavaGenericTypeArgument argument) {
		this.genericArguments.add(argument);
	}

	/** {@inheritDoc} */
	@Override
	public void addAnnotation(JavaAnnotation annotation) {
		super.addAnnotation(annotation);
		this.imports.add(annotation.getType().getFullyQualifiedName());
	}

	/**
	 * <p>getFullyQualifiedName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFullyQualifiedName() {
		return this.packagePath + "." + this.getName();
	}

	/** {@inheritDoc} */
	@Override
	public Set<String> getImports() {
		final Set<String> allImports = super.getImports();
		allImports.addAll(interfaces.stream().flatMap(i -> i.getImports().stream()).collect(Collectors.toSet()));
		allImports.addAll(methods.stream().flatMap(i -> i.getImports().stream()).collect(Collectors.toSet()));

		if (superClass != null) {
			allImports.addAll(superClass.getImports());
		}

		return allImports;
	}
}
