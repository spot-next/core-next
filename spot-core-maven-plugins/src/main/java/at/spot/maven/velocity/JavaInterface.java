package at.spot.maven.velocity;

import java.util.ArrayList;
import java.util.List;

public class JavaInterface extends AbstractJavaObject {
	private static final long serialVersionUID = 1L;

	protected String packagePath;
	protected JavaInterface superClass;
	protected final List<JavaInterface> interfaces = new ArrayList<>();
	protected final List<JavaMethod> methods = new ArrayList<>();

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
	}

	public List<JavaInterface> getInterfaces() {
		return interfaces;
	}

	public List<JavaMethod> getMethods() {
		return methods;
	}
}
