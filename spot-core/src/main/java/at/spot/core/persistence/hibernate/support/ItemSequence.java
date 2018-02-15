package at.spot.core.persistence.hibernate.support;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}

}
