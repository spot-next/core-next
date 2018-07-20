package at.spot.maven.velocity.type.annotation;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import at.spot.maven.velocity.type.AbstractObject;
import at.spot.maven.velocity.type.parts.Expression;
import at.spot.maven.velocity.type.parts.JavaMemberType;

public class JavaAnnotation extends AbstractObject {
	private static final long serialVersionUID = 1L;

	protected JavaMemberType type;
	protected Map<String, Object> parameters = new HashMap<>();

	public JavaAnnotation(final Class<? extends Annotation> type) {
		super(type.getSimpleName());
		this.type = new JavaMemberType(type);
		this.imports.add(type.getName());
	}

	public JavaAnnotation(JavaMemberType type) {
		super(type.getName());
		this.type = type;
	}

	public JavaAnnotation(final String name, final String packagePath) {
		super(name);
		this.type = new JavaMemberType(name, packagePath);
	}

	public JavaMemberType getType() {
		return type;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(final Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(final String key, final Class<?> value) {
		addParameter(key, value.getName(), ValueType.CLASS);
	}

	public void addParameter(final String key, final Object value, final ValueType valueType) {
		Expression val = new Expression(value, valueType);
		this.parameters.put(key, val);
	}

	@Override
	public Set<String> getImports() {
		final Set<String> allImports = super.getImports();

		allImports.addAll(type.getImports());

		for (Object val : parameters.values()) {
			if (val instanceof Class) {
				allImports.add(((Class<?>) val).getName());
			} else {
				allImports.add(val.getClass().getName());
			}
		}

		return allImports;
	}
}
