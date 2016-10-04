package at.spot.core.data.model;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.DateTime;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.annotation.model.Type;
import at.spot.core.infrastructure.exception.PropertyNotAccessibleException;
import at.spot.core.infrastructure.type.PK;

@Type
public abstract class Item implements Serializable {

	private static final long serialVersionUID = 1L;

	private boolean persisted;
	private boolean dirty;

	@Property(unique = true)
	public PK pk;

	@Property
	public DateTime lastModified;

	@Property
	public DateTime created;

	public Object getProperty(String propertyName) throws PropertyNotAccessibleException {
		try {
			return BeanUtils.getSimpleProperty(this, propertyName);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new PropertyNotAccessibleException(e);
		}
	}

	public void setProperty(String propertyName, Object value) throws PropertyNotAccessibleException {
		try {
			BeanUtils.setProperty(this, propertyName, value);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new PropertyNotAccessibleException(e);
		}
	}

	public boolean isPersisted() {
		return pk != null && persisted;
	}

	public boolean isDirty() {
		return dirty;
	}

}
