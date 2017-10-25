package at.spot.maven.velocity.type.annotation;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import at.spot.maven.velocity.type.AbstractObject;
import at.spot.maven.velocity.type.parts.JavaMemberType;

public class JavaAnnotation extends AbstractObject {
	private static final long serialVersionUID = 1L;

	protected JavaMemberType type;
	protected Map<String, Object> parameters = new HashMap<>();

	public JavaAnnotation(final Class<? extends Annotation> type) {
		super(type.getSimpleName());
		this.type = new JavaMemberType(type);
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
		addParameter(key, value.getName(), AnnotationValueType.CLASS);
	}

	public void addParameter(final String key, final Object value, final AnnotationValueType valueType) {
		Object val = null;

		if (AnnotationValueType.BOOLEAN.equals(valueType)) {
			val = value.toString();
		} else if (AnnotationValueType.STRING.equals(valueType)) {
			val = "\"" + value + "\"";
		} else if (AnnotationValueType.CLASS.equals(valueType)) {
			val = value + ".class";
		} else if (AnnotationValueType.ENUM_VALUE.equals(valueType) && value instanceof Enum) {
			val = value.getClass().getName() + "." + ((Enum<?>) value).name();
		} else if (AnnotationValueType.LITERAL.equals(valueType)) {
			val = value;
		} else if (AnnotationValueType.STRING_ARRAY.equals(valueType)) {
			val = String.format("{%s}", StringUtils
					.join(Stream.of((String[]) value).map(e -> "\"" + e + "\"").collect(Collectors.toList()), ","));
		} else if (AnnotationValueType.LITERAL_ARRAY.equals(valueType)) {
			val = String.format("{%s}", StringUtils.join((String[]) value));
		}

		this.parameters.put(key, val);
	}

	@Override
	public Set<String> getImports() {
		final Set<String> allImports = super.getImports();

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
