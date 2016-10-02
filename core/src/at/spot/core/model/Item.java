package at.spot.core.model;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.joda.time.DateTime;

import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.annotation.model.Type;
import at.spot.core.infrastructure.exception.PropertyNotAccessibleException;

@Type
public abstract class Item implements Serializable {

	private static final long serialVersionUID = 1L;

	@Property(unique = true)
	public long pk;

	@Property
	public DateTime lastModified;

	@Property
	public DateTime created;

	public Object getProperty(String propertyName) throws PropertyNotAccessibleException {
		try {
			return BeanUtils.getSimpleProperty(this, propertyName);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new PropertyNotAccessibleException();
		}
	}

	public void setProperty(String propertyName, Object value) throws PropertyNotAccessibleException {
		try {
			BeanUtils.setProperty(this, propertyName, value);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new PropertyNotAccessibleException();
		}
	}
}
