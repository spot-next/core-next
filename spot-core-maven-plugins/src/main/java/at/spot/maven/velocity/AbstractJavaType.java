package at.spot.maven.velocity;

import java.io.Serializable;

public abstract class AbstractJavaType implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
