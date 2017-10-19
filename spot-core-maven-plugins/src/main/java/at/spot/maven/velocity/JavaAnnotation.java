package at.spot.maven.velocity;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public class JavaAnnotation {
	protected JavaMemberType type;
	protected Map<String, Object> parameters = new HashMap<>();

	public JavaMemberType getType() {
		return type;
	}

	public void setType(final Class<? extends Annotation> type) {
		this.type = new JavaMemberType(type);
	}

	public void setType(final String name, final String packagePath) {
		this.type = new JavaMemberType(name, packagePath);
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

		if (AnnotationValueType.STRING.equals(valueType)) {
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
}
