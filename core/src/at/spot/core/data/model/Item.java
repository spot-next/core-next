package at.spot.core.data.model;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.joda.time.DateTime;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.exception.PropertyNotAccessibleException;
import at.spot.core.infrastructure.type.PK;

@ItemType
public abstract class Item implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> dirtyAttributes = new ArrayList<>();

	@Property(unique = true)
	public PK pk;

	@Property
	public DateTime lastModified;

	@Property
	public DateTime created;

	/**
	 * If this object is used as a proxy, eg. in a collection or relation, this
	 * is true. The item property handler then knows it has to load it on the
	 * fly.
	 */
	public final boolean isProxy;

	public Item() {
		this.isProxy = false;
	}

	public Item(boolean isProxy) {
		this.isProxy = isProxy;
	}

	public Object getProperty(String propertyName) throws PropertyNotAccessibleException {
		try {
			return BeanUtils.getSimpleProperty(this, propertyName);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new PropertyNotAccessibleException(e);
		}
	}

	public void setProperty(String propertyName, Object value) throws PropertyNotAccessibleException {
		try {
			FieldUtils.writeField(this, propertyName, value);
		} catch (IllegalAccessException e) {
			//
		}
	}

	public boolean isPersisted() {
		return pk != null && dirtyAttributes.size() == 0;
	}

	public boolean isDirty() {
		return dirtyAttributes.size() > 0;
	}

	private void markDirty(String propertyName) {
		this.dirtyAttributes.add(propertyName);
	}
}
