package at.spot.maven.velocity;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class JavaAnnotation {
	protected Class<? extends Annotation> type;
	protected Map<String, Object> parameters = new HashMap<>();

	public Class<? extends Annotation> getType() {
		return type;
	}

	public void setType(Class<? extends Annotation> type) {
		this.type = type;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
}
