package at.spot.maven.velocity.type;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import at.spot.maven.velocity.type.annotation.JavaAnnotation;
import at.spot.maven.velocity.type.base.JavaInterface;
import at.spot.maven.velocity.type.parts.JavaGenericTypeArgument;
import at.spot.maven.velocity.type.parts.JavaMethod;
import at.spot.maven.velocity.type.parts.JavaMethodArgument;

public abstract class AbstractComplexJavaType extends AbstractJavaObject {
	private static final long serialVersionUID = 1L;

	protected final Set<JavaInterface> interfaces = new HashSet<>();
	protected final Set<JavaMethod> methods = new HashSet<>();
	protected final Set<JavaGenericTypeArgument> genericArguments = new HashSet<>();

	protected String packagePath;
	protected JavaInterface superClass;

	public AbstractComplexJavaType() {
	}

	public AbstractComplexJavaType(String name, String packagePath) {
		setName(name);
		setPackagePath(packagePath);
	}

	public AbstractComplexJavaType(Class<?> clazz) {
		this(clazz.getSimpleName(), clazz.getPackage().getName());
	}

	public String getPackagePath() {
		return packagePath;
	}

	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}

	public AbstractComplexJavaType getSuperClass() {
		return superClass;
	}

	public void setSuperClass(JavaInterface superClass) {
		this.superClass = superClass;
		this.imports.add(superClass.getFullyQualifiedName());
	}

	public void setSuperClass(Class<?> superClass) {
		setSuperClass(new JavaInterface(superClass.getPackage().getName(), superClass.getSimpleName()));
	}

	public Set<JavaInterface> getInterfaces() {
		return Collections.unmodifiableSet(interfaces);
	}

	public Set<JavaMethod> getMethods() {
		return Collections.unmodifiableSet(methods);
	}

	public void addInterface(JavaInterface iface) {
		this.interfaces.add(iface);
		this.imports.add(iface.getFullyQualifiedName());
	}

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

	public Set<JavaGenericTypeArgument> getGenericArguments() {
		return genericArguments;
	}

	public void addGenericArgument(final JavaGenericTypeArgument argument) {
		this.genericArguments.add(argument);
	}

	@Override
	public void addAnnotation(JavaAnnotation annotation) {
		super.addAnnotation(annotation);
		this.imports.add(annotation.getType().getFullyQualifiedName());
	}

	public String getFullyQualifiedName() {
		return this.packagePath + "." + this.getName();
	}

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
