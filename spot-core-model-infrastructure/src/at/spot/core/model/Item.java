package at.spot.core.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.type.ExtendedAttributes;
import at.spot.core.support.util.ClassUtil;

@ItemType
public abstract class Item implements Serializable {

	private static final long serialVersionUID = 1L;

	protected transient boolean forceDirty = false;
	protected transient List<String> dirtyAttributes = new ArrayList<>();

	public Long pk;

	@Property
	public DateTime lastModified;

	@Property
	public DateTime created;

	protected String typeCode;

	@Property
	protected Map<Class<ExtendedAttributes>, ExtendedAttributes> extendedAttributes = new HashMap<>();

	/**
	 * If this object is used as a proxy, eg. in a collection or relation, this
	 * is true. The item property handler then knows it has to load it on the
	 * fly.
	 */
	public final boolean isProxy;

	public Item() {
		this.created = new DateTime();
		this.isProxy = false;
	}

	public Item(final boolean isProxy) {
		this.isProxy = isProxy;
	}

	/**
	 * This property hold {@link ExtendedAttributes} objects. It's an easy way
	 * to customize an {@link ItemType} object without subclassing it.<br />
	 * 
	 * @param attributeType
	 * @return
	 * @throws InstantiationException
	 */
	public <E extends ExtendedAttributes> E getExtendedAttribute(final Class<E> attributeType)
			throws InstantiationException {
		E ea = (E) extendedAttributes.get(attributeType);

		if (ea == null) {
			try { // create new object in case there is none
				ea = attributeType.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new InstantiationException("Could not instantiate extended attribute type");
			}

			extendedAttributes.put((Class<ExtendedAttributes>) attributeType, ea);
		}

		return ea;
	}

	public Object getProperty(final String propertyName) {
		return ClassUtil.getField(this, propertyName, true);
	}

	public void setProperty(final String propertyName, final Object value) {
		ClassUtil.setField(this, propertyName, value);
	}

	/**
	 * @return true if the item has a PK. It is assumed that it has been saved
	 *         before. If you set a PK manually and save the item, an existing
	 *         item with the same PK will be overwritten.
	 */
	public boolean isPersisted() {
		return pk != null;
	}

	public boolean isDirty() {
		return forceDirty | dirtyAttributes.size() > 0;
	}

	public void markAsDirty(final String propertyName) {
		this.dirtyAttributes.add(propertyName);
		this.lastModified = new DateTime();
	}

	/**
	 * Mark the object as dirty, even though it might no be.
	 */
	public void markAsDirty() {
		this.forceDirty = true;
	}

	protected void clearDirtyFlag() {
		this.dirtyAttributes.clear();
	}

	/**
	 * Returns the names and the values of all properties annotated
	 * with @Unique.
	 * 
	 * @return
	 */
	public Map<String, Object> getUniqueProperties() {
		final Map<String, Object> uniqueProps = new HashMap<>();

		for (final Field uniqueField : ClassUtil.getFieldsWithAnnotation(this, Property.class)) {
			final Property prop = ClassUtil.getAnnotation(uniqueField, Property.class);

			if (prop.unique()) {
				uniqueProps.put(uniqueField.getName(), ClassUtil.getField(this, uniqueField.getName(), true));
			}
		}

		return uniqueProps;
	}

	/**
	 * Returns a hash code calculated of all properties that are defined as
	 * unique (with the {@link Property} annotation).
	 * 
	 * @return
	 */
	public int uniquenessHash() {
		int hash = 0;

		hash = getUniqueProperties().hashCode();

		return hash;
	}

}
