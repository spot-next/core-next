package io.spotnext.core.persistence.hibernate.support;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * <p>ItemSequence class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "item_sequence")
public class ItemSequence implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	protected String name;

	protected long value = 0;

	// public ItemSequence(String sequenceName, long value) {
	// this.sequenceName = sequenceName;
	// this.value = value;
	// }

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
	 * <p>Getter for the field <code>value</code>.</p>
	 *
	 * @return a long.
	 */
	public long getValue() {
		return value;
	}

	/**
	 * <p>Setter for the field <code>value</code>.</p>
	 *
	 * @param value a long.
	 */
	public void setValue(long value) {
		this.value = value;
	}

}
