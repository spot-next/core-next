package at.spot.maven.velocity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public abstract class AbstractJavaObject extends AbstractJavaType {
	private static final long serialVersionUID = 1L;

	protected String description;
	protected Visibility visiblity = Visibility.PUBLIC;
	protected final Set<JavaAnnotation> annotations = new HashSet<>();

	public void addAnnotation(JavaAnnotation annotation) {
		this.annotations.add(annotation);
	}

	public String getDescription() {
		return description;
	}

	public String getJavadoc() {
		return StringUtils.replace(description, "\n", "<br>");
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<JavaAnnotation> getAnnotations() {
		return Collections.unmodifiableSet(annotations);
	}

	public Visibility getVisiblity() {
		return visiblity;
	}

	public void setVisiblity(Visibility visiblity) {
		this.visiblity = visiblity;
	}
}
