package io.spotnext.maven.velocity.type.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.spotnext.maven.util.MiscUtil;
import io.spotnext.maven.velocity.type.AbstractObject;

/**
 * <p>JavaMemberType class.</p>
 *
 * @since 1.0
 */
public class JavaMemberType extends AbstractObject {
	private static final long serialVersionUID = 1L;

	protected final List<JavaGenericTypeArgument> genericArguments = new ArrayList<>();
	protected String packagePath = null;
	protected boolean isArray = false;

	/**
	 * <p>Constructor for JavaMemberType.</p>
	 */
	public JavaMemberType() {
	}

	/**
	 * If the given name contains a '.', the name will be treated as a fully
	 * qualified class name and both name and package path will be populated.
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public JavaMemberType(final String name) {
		if (name.contains(".")) {
			this.name = MiscUtil.getClassName(name);
			this.packagePath = MiscUtil.getClassPackage(name);
			getImports().add(name);
		} else {
			this.name = name;
		}
	}

	/**
	 * <p>isPrimitiveType.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isPrimitiveType() {
		return !name.contains(".");
	}

	/**
	 * <p>Constructor for JavaMemberType.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param packagePath a {@link java.lang.String} object.
	 */
	public JavaMemberType(final String name, final String packagePath) {
		this(name);
		this.packagePath = packagePath;
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
	public void setPackagePath(final String packagePath) {
		this.packagePath = packagePath;
	}

	/**
	 * <p>Constructor for JavaMemberType.</p>
	 *
	 * @param type a {@link java.lang.Class} object.
	 */
	public JavaMemberType(final Class<?> type) {
		this(type.getSimpleName());
		this.packagePath = type.getPackage().getName();
	}

	/**
	 * <p>Getter for the field <code>genericArguments</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<JavaGenericTypeArgument> getGenericArguments() {
		return Collections.unmodifiableList(genericArguments);
	}

	/**
	 * <p>addGenericArgument.</p>
	 *
	 * @param argument a {@link io.spotnext.maven.velocity.type.parts.JavaGenericTypeArgument} object.
	 */
	public void addGenericArgument(final JavaGenericTypeArgument argument) {
		this.genericArguments.add(argument);
	}

	/**
	 * <p>getFullyQualifiedName.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFullyQualifiedName() {
		return (StringUtils.isNotBlank(this.packagePath) ? packagePath + "." : "") + this.name;
	}

	/**
	 * <p>isComplexType.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isComplexType() {
		return StringUtils.isNotBlank(packagePath);
	}

	/** {@inheritDoc} */
	@Override
	public Set<String> getImports() {
		final Set<String> allImports = super.getImports();

		allImports.addAll(
				genericArguments.stream().flatMap(g -> g.getType().getImports().stream()).collect(Collectors.toSet()));
		if (isComplexType()) {
			allImports.add(this.getFullyQualifiedName());
		}

		return allImports;
	}

	/**
	 * <p>isArray.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isArray() {
		return isArray;
	}

	/**
	 * <p>setArray.</p>
	 *
	 * @param isArray a boolean.
	 */
	public void setArray(final boolean isArray) {
		this.isArray = isArray;
	}

	/*****************************************************************************************
	 *************************************************************************************** */

	public static final JavaMemberType VOID = new JavaMemberType("void");
}
