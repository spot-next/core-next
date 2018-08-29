package io.spotnext.maven.velocity.type;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Abstract AbstractObject class.</p>
 *
 * @since 1.0
 */
public abstract class AbstractObject implements Serializable {
	private static final long serialVersionUID = 1L;

	protected final Set<String> imports = new HashSet<>();
	protected String name;

	/**
	 * <p>Constructor for AbstractObject.</p>
	 */
	public AbstractObject() {
	}

	/**
	 * <p>Constructor for AbstractObject.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public AbstractObject(String name) {
		this.name = name;
	}

	/**
	 * <p>Getter for the field <code>name</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return name;
	}

	/**
	 * <p>Setter for the field <code>name</code>.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the list of types to import.
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<String> getImports() {
		return imports;
	}
}
