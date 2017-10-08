package at.spot.maven.velocity;

import java.io.Serializable;

public class JavaEnumValue implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String description;
	protected String name;
	protected String internalName;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInternalName() {
		return internalName;
	}

	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
