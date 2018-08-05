package io.spotnext.maven.velocity.type.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.spotnext.maven.util.MiscUtil;
import io.spotnext.maven.velocity.type.AbstractObject;

public class JavaMemberType extends AbstractObject {
	private static final long serialVersionUID = 1L;

	protected final List<JavaGenericTypeArgument> genericArguments = new ArrayList<>();
	protected String packagePath = null;
	protected boolean isArray = false;

	public JavaMemberType() {
	}

	/**
	 * If the given name contains a '.', the name will be treated as a fully
	 * qualified class name and both name and package path will be populated.
	 * 
	 * @param name
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

	public JavaMemberType(final String name, final String packagePath) {
		this(name);
		this.packagePath = packagePath;
	}

	public String getPackagePath() {
		return packagePath;
	}

	public void setPackagePath(final String packagePath) {
		this.packagePath = packagePath;
	}

	public JavaMemberType(final Class<?> type) {
		this(type.getSimpleName());
		this.packagePath = type.getPackage().getName();
	}

	public List<JavaGenericTypeArgument> getGenericArguments() {
		return Collections.unmodifiableList(genericArguments);
	}

	public void addGenericArgument(final JavaGenericTypeArgument argument) {
		this.genericArguments.add(argument);
	}

	public String getFullyQualifiedName() {
		return (StringUtils.isNotBlank(this.packagePath) ? packagePath + "." : "") + this.name;
	}

	public boolean isComplexType() {
		return StringUtils.isNotBlank(packagePath);
	}

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

	public boolean isArray() {
		return isArray;
	}

	public void setArray(final boolean isArray) {
		this.isArray = isArray;
	}

	/*****************************************************************************************
	 *************************************************************************************** */

	public static final JavaMemberType VOID = new JavaMemberType("void");
}
