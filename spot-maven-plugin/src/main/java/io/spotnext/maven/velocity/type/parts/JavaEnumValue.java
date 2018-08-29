package io.spotnext.maven.velocity.type.parts;

import java.io.Serializable;

/**
 * <p>JavaEnumValue class.</p>
 *
 * @since 1.0
 */
public class JavaEnumValue implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String description;
	protected String name;
	protected String internalName;

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
	 * <p>Getter for the field <code>internalName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getInternalName() {
		return internalName;
	}

	/**
	 * <p>Setter for the field <code>internalName</code>.</p>
	 *
	 * @param internalName a {@link java.lang.String} object.
	 */
	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}

	/**
	 * <p>Getter for the field <code>description</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <p>Setter for the field <code>description</code>.</p>
	 *
	 * @param description a {@link java.lang.String} object.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
