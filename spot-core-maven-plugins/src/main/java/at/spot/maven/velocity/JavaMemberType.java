package at.spot.maven.velocity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class JavaMemberType extends AbstractJavaType {
	private static final long serialVersionUID = 1L;

	protected final List<AbstractComplexJavaType> genericArguments = new ArrayList<>();
	protected String packagePath = null;;

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

	public List<AbstractComplexJavaType> getGenericArguments() {
		return Collections.unmodifiableList(genericArguments);
	}

	public void addGenericArgument(AbstractComplexJavaType argument) {
		this.genericArguments.add(argument);
	}

	public String getFullyQualifiedName() {
		return (StringUtils.isNotBlank(this.packagePath) ? packagePath + "." : "") + this.name;
	}

	public boolean isComplexType() {
		return StringUtils.isNotBlank(packagePath);
	}

	public static final JavaMemberType VOID = new JavaMemberType("void");
}
