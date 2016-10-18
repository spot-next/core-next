package at.spot.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	}

	protected void clearDirtyFlag() {
		this.dirtyAttributes.clear();
	}

	@Override
	public void notify(String collectionName, ObservableChange change, Object element) {
		// TODO: this should be done directly in the itempropertyaccessaspect
		markAsDirty(collectionName);
	}
}
