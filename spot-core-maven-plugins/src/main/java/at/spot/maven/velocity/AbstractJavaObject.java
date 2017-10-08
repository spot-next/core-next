package at.spot.maven.velocity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractJavaObject implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String description;
	protected Visibility visiblity = Visibility.PUBLIC;
	protected String name;
	protected final List<JavaAnnotation> annotations = new ArrayList<>();

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<JavaAnnotation> getAnnotations() {
		return Collections.unmodifiableList(annotations);
	}

	public Visibility getVisiblity() {
		return visiblity;
	}

	public void setVisiblity(Visibility visiblity) {
		this.visiblity = visiblity;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
