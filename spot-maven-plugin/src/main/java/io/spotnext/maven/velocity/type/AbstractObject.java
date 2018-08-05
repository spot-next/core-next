package io.spotnext.maven.velocity.type;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractObject implements Serializable {
	private static final long serialVersionUID = 1L;

	protected final Set<String> imports = new HashSet<>();
	protected String name;

	public AbstractObject() {
	}

	public AbstractObject(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the list of types to import.
	 */
	public Set<String> getImports() {
		return imports;
	}
}
