package at.spot.core.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import at.spot.core.infrastructure.annotation.model.ItemType;
import at.spot.core.infrastructure.annotation.model.Property;
import at.spot.core.infrastructure.type.PK;
import at.spot.core.infrastructure.type.collection.ObservableChange;
import at.spot.core.infrastructure.type.collection.Observer;
import at.spot.core.support.util.ClassUtil;

@ItemType
public abstract class Item implements Serializable, Observer {

	private static final long serialVersionUID = 1L;

	protected List<String> dirtyAttributes = new ArrayList<>();

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
		this.created = new DateTime();
		this.isProxy = false;
	}

	public Item(boolean isProxy) {
		this.isProxy = isProxy;
	}

	public Object getProperty(String propertyName) {
		return ClassUtil.getPrivateField(this, propertyName);
	}

	public void setProperty(String propertyName, Object value) {
		ClassUtil.setField(this, propertyName, value);
	}

	public boolean isPersisted() {
		return pk != null && dirtyAttributes.size() == 0;
	}

	public boolean isDirty() {
		return dirtyAttributes.size() > 0;
	}

	protected void markAsDirty(String propertyName) {
		this.dirtyAttributes.add(propertyName);
		this.lastModified = new DateTime();
	}

	protected void clearDirtyFlag() {
		this.dirtyAttributes.clear();
	}

	@Override
	public void notify(String collectionName, ObservableChange change, Object element) {
		// TODO: this should be done directly in the itempropertyaccessaspect
		markAsDirty(collectionName);
	}

	/**
	 * Returns the names and the values of all properties annotated
	 * with @Unique.
	 * 
	 * @return
	 */
	public Map<String, Object> getUniqueProperties() {
		Map<String, Object> uniqueProps = new HashMap<>();

		for (Field uniqueField : ClassUtil.getFieldsWithAnnotation(this, Property.class)) {
			Property prop = ClassUtil.getAnnotation(uniqueField, Property.class);

			if (prop.unique()) {
				uniqueProps.put(uniqueField.getName(), ClassUtil.getPrivateField(this, uniqueField.getName()));
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
