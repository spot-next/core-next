package at.spot.maven.velocity.type.annotation;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import at.spot.maven.velocity.type.AbstractObject;
import at.spot.maven.velocity.type.parts.JavaExpression;
import at.spot.maven.velocity.type.parts.JavaMemberType;

public class JavaAnnotation extends AbstractObject {
	private static final long serialVersionUID = 1L;

	protected JavaMemberType type;
	protected Map<String, JavaExpression> parameters = new HashMap<>();

	public JavaAnnotation(final Class<? extends Annotation> type) {
		super(type.getSimpleName());
		this.type = new JavaMemberType(type);
		this.imports.add(type.getName());
	}

	public JavaAnnotation(final JavaMemberType type) {
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

	public Map<String, JavaExpression> getParameters() {
		return parameters;
	}

	public void setParameters(final Map<String, JavaExpression> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(final String key, final Class<?> value) {
		addParameter(key, value.getName(), JavaValueType.CLASS);
	}

	public void addParameter(final String key, final Object value, final JavaValueType valueType) {
		final JavaExpression val = new JavaExpression(value, valueType);
		this.parameters.put(key, val);
	}

	@Override
	public Set<String> getImports() {
		final Set<String> allImports = super.getImports();

		allImports.addAll(type.getImports());

		for (final JavaExpression val : parameters.values()) {
			allImports.add(val.getValue().getClass().getName());
		}

		return allImports;
	}
}
