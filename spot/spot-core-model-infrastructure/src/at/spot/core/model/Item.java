package at.spot.core.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.support.util.ClassUtil;

public abstract class Item implements Serializable {

	private static final long serialVersionUID = 1L;

	protected transient boolean forceDirty = false;
	protected transient List<String> dirtyAttributes = new ArrayList<>();

	public Long pk;
	protected String typeCode;

	@Property
	public DateTime lastModifiedAt;

	@Property
	public DateTime createdAt;

	/**
	 * If this object is used as a proxy, eg. in a collection or relation, this
	 * is true. The item property handler then knows it has to load it on the
	 * fly.
	 */
	public final boolean isProxy;

	public Item() {
		this.createdAt = new DateTime();
		this.isProxy = false;
	}

	public Item(final boolean isProxy) {
		this.isProxy = isProxy;
	}

	public Long getPk() {
		return pk;
	}

	public DateTime getLastModifiedAt() {
		return lastModifiedAt;
	}

	public DateTime getCreatedAt() {
		return createdAt;
	}

	public boolean isProxy() {
		return isProxy;
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
		this.lastModifiedAt = new DateTime();
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

		for (final Field uniqueField : ClassUtil.getFieldsWithAnnotation(this.getClass(), Property.class)) {
			final Property prop = ClassUtil.getAnnotation(uniqueField, Property.class);

			if (prop.unique()) {
				Object propertyValue = ClassUtil.getField(this, uniqueField.getName(), true);

				if (propertyValue instanceof Item) {
					propertyValue = ((Item) propertyValue).uniquenessHash();
				}

				uniqueProps.put(uniqueField.getName(), propertyValue);
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

	/**
	 * If the type and the pk of the given object is the same as the current
	 * object, both are equal.
	 * 
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && this.getClass().equals(obj.getClass())) {
			return this.pk == ((Item) obj).pk;
		}

		return super.equals(obj);
	}
}
