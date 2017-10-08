package at.spot.maven.velocity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractJavaObject implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String comment;
	protected Visibility visiblity;
	protected String name;
	protected final List<JavaAnnotation> annotations = new ArrayList<>();

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<JavaAnnotation> getAnnotations() {
		return annotations;
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
