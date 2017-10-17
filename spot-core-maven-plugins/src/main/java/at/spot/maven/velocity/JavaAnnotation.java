package at.spot.maven.velocity;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class JavaAnnotation {
	protected JavaMemberType type;
	protected Map<String, Object> parameters = new HashMap<>();

	public JavaMemberType getType() {
		return type;
	}

	public void setType(Class<? extends Annotation> type) {
		this.type = new JavaMemberType(type);
	}

	public void setType(String name, String packagePath) {
		this.type = new JavaMemberType(name, packagePath);
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(String key, Object value) {
		this.parameters.put(key, value);
	}
}
