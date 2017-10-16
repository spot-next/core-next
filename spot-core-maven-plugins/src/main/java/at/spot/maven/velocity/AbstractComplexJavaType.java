package at.spot.maven.velocity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractComplexJavaType extends AbstractJavaObject {
	private static final long serialVersionUID = 1L;

	protected final Set<String> imports = new HashSet<>();
	protected final Set<JavaInterface> interfaces = new HashSet<>();
	protected final Set<JavaMethod> methods = new HashSet<>();

	protected String packagePath;
	protected JavaInterface superClass;

	public String getPackagePath() {
		return packagePath;
	}

	public void setPackagePath(String packagePath) {
		this.packagePath = packagePath;
	}

	public JavaInterface getSuperClass() {
		return superClass;
	}

	public void setSuperClass(JavaInterface superClass) {
		this.superClass = superClass;
		this.imports.add(superClass.getFullyQualifiedName());
	}

	public void setSuperClass(Class<?> superClass) {
		JavaClass javaSuperClass = new JavaClass();
		javaSuperClass.setPackagePath(superClass.getPackage().getName());
		javaSuperClass.setName(superClass.getSimpleName());

		this.imports.add(superClass.getName());
	}

	public Set<String> getImports() {
		return Collections.unmodifiableSet(imports);
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

		for (Map.Entry<String, Class<?>> entry : method.getArguments().entrySet()) {
			this.imports.add(entry.getValue().getName());
		}

		if (method.getType().isComplexType()) {
			this.imports.add(method.getType().getFullyQualifiedName());
		}
	}

	@Override
	public void addAnnotation(JavaAnnotation annotation) {
		super.addAnnotation(annotation);
		this.imports.add(annotation.getType().getName());
	}

	public String getFullyQualifiedName() {
		return this.packagePath + "." + this.getName();
	}
}
