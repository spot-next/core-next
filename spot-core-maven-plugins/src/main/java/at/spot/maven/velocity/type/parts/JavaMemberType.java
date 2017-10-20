package at.spot.maven.velocity.type.parts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import at.spot.maven.velocity.type.AbstractObject;

public class JavaMemberType extends AbstractObject {
	private static final long serialVersionUID = 1L;

	protected final List<JavaGenericTypeArgument> genericArguments = new ArrayList<>();
	protected String packagePath = null;

	public JavaMemberType() {
	}

	public JavaMemberType(String name) {
		this.name = name;
	}

	public JavaMemberType(String name, String packagePath) {
		this(name);
		this.packagePath = packagePath;
	}

	public String getPackagePath() {
		return packagePath;
	}

	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}

	public JavaMemberType(Class<?> type) {
		this(type.getSimpleName());
		this.packagePath = type.getPackage().getName();
	}

	public List<JavaGenericTypeArgument> getGenericArguments() {
		return Collections.unmodifiableList(genericArguments);
	}

	public void addGenericArgument(JavaGenericTypeArgument argument) {
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

	/*****************************************************************************************
	 *************************************************************************************** */

	public static final JavaMemberType VOID = new JavaMemberType("void");
}
