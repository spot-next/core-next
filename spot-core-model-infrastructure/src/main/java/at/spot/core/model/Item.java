package at.spot.core.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.VersionStrategy;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.collections4.comparators.NullComparator;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.datanucleus.api.jdo.annotations.CreateTimestamp;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.support.util.ClassUtil;

//JDO
@PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.NEW_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER)
// JPA
// @MappedSuperclass
@Entity
@DiscriminatorColumn(name = "type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class Item implements Serializable, Comparable<Item> {

	private static final long serialVersionUID = 1L;

	protected final transient Map<String, Boolean> propertiesInitializationState = new HashMap<>();

	@Transient
	protected transient boolean forceDirty = false;
	@Transient
	protected final List<String> dirtyAttributes = new ArrayList<>();

	// JDO
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
	// JPA
	@Id
	@GeneratedValue
	protected Long pk;

	@Transient
	protected String typeCode;

	@Property
	@org.datanucleus.api.jdo.annotations.UpdateTimestamp
	@UpdateTimestamp
	protected Date lastModifiedAt;

	@Property
	@CreateTimestamp
	@CreationTimestamp
	protected Date createdAt;

	@Version
	protected long version;

	/**
	 * If this object is used as a proxy, eg. in a collection or relation, this is
	 * true. The item property handler then knows it has to load it on the fly.
	 */
	public transient boolean isProxy;

	public Item() {
		this.isProxy = false;
	}

	public Item(final boolean isProxy) {
		this.isProxy = isProxy;
	}

	public void setPk(final Long pk) {
		this.pk = pk;
	}

	public Long getPk() {
		return pk;
	}

	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public boolean isProxy() {
		return isProxy;
	}

	public void setIsProxy(final boolean isProxy) {
		this.isProxy = isProxy;
	}

	/**
	 * @return true if the item has a PK. It is assumed that it has been saved
	 *         before. If you set a PK manually and save the item, an existing item
	 *         with the same PK will be overwritten.
	 */
	public boolean isPersisted() {
		return pk != null;
	}

	public List<String> getDirtyAttributes() {
		return Collections.unmodifiableList(dirtyAttributes);
	}

	public boolean isDirty() {
		return forceDirty | dirtyAttributes.size() > 0;
	}

	public void markAsDirty(final String propertyName) {
		this.dirtyAttributes.add(propertyName);
		this.lastModifiedAt = new Date();
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
	 * Returns the names and the values of all properties annotated with @Unique.
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
	 * Returns a hash code calculated of all properties that are defined as unique
	 * (with the {@link Property} annotation).
	 *
	 * @return
	 */
	public int uniquenessHash() {
		int hash = 0;

		hash = getUniqueProperties().hashCode();

		return hash;
	}

	/**
	 * If the type and the pk of the given object is the same as the current object,
	 * both are equal.
	 *
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null || !(obj.getClass().equals(this.getClass()))) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		return this.pk != null ? this.pk.equals(((Item) obj).pk) : super.equals(obj);
	}

	@Override
	public int hashCode() {
		final HashCodeBuilder hcb = new HashCodeBuilder();
		hcb.append(this.pk);
		return hcb.toHashCode();
	}

	@Override
	public int compareTo(final Item obj) {
		if (equals(obj)) {
			return 0;
		}

		if (obj != null) {
			return Objects.compare(this.pk, obj.pk, new NullComparator<Long>());
		} else {
			return 1;
		}
	}

	public boolean isPropertyInitialized(String propertyName) {
		return propertiesInitializationState.get(propertyName) != null;
	}

	public void setPropertyInitialized(String propertyName) {
		propertiesInitializationState.put(propertyName, Boolean.TRUE);
	}
}
