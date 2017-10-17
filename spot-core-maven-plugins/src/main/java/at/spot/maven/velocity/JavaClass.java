package at.spot.maven.velocity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TemplateFile("class.vm")
public class JavaClass extends AbstractComplexJavaType {
	private static final long serialVersionUID = 1L;

	protected final List<JavaField> fields = new ArrayList<>();

	public JavaClass() {
	}

	public JavaClass(String name, String packagePath) {
		setName(name);
		setPackagePath(packagePath);
	}

	public JavaClass(Class<?> clazz) {
		setName(clazz.getSimpleName());
		setPackagePath(clazz.getPackage().getName());
	}

	public List<JavaField> getFields() {
		return Collections.unmodifiableList(fields);
	}

	public void addField(JavaField field) {
		this.fields.add(field);

		if (field.getType().isComplexType()) {
			this.imports.add(field.getType().getFullyQualifiedName());
		}

		for (JavaAnnotation a : field.getAnnotations()) {
			this.imports.add(a.getType().getFullyQualifiedName());
		}
	}
}
