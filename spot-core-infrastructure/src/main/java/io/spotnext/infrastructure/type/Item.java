package io.spotnext.infrastructure.type;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import org.apache.commons.collections4.comparators.NullComparator;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.spotnext.infrastructure.IdGenerator;
import io.spotnext.infrastructure.annotation.ItemType;
import io.spotnext.infrastructure.annotation.Property;
import io.spotnext.support.util.ClassUtil;

// Hibernate
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region = "items")
// JPA
@Cacheable
@MappedSuperclass
// @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EntityListeners({ AuditingEntityListener.class })
public abstract class Item implements Serializable, Comparable<Item> {

	private static final long serialVersionUID = 1L;
	public static final String TYPECODE = "item";
	public static final String PROPERTY_PK = "pk";
	public static final String PROPERTY_LAST_MODIFIED_AT = "lastModifiedAt";
	public static final String PROPERTY_CREATED_AT = "createdAt";

	// @Autowired
	// protected AuditingHandler auditingHandler;

	// JPA
	@Id
	@Column(name = "pk")
	final protected Long pk = IdGenerator.createLongId();

//	@Index(name = "idx_createdAt")
	@CreationTimestamp
	@CreatedDate
	protected Date createdAt;

	@CreatedBy
	protected String createdBy;

//	@Index(name = "idx_lastModifiedAt")
	@UpdateTimestamp
	@LastModifiedDate
	// the index is needed for ORDER BY in combination with FETCH JOINS and pagination!
//	@Property(indexed = true)
	@Index(name = "idx_Item_lastModifiedAt")
	protected Date lastModifiedAt;

	@LastModifiedBy
	protected String lastModifiedBy;

	@Version
	protected int version = -1;

	/**
	 * Indicates that the given item is deleted. This property can be used to implemented "soft-deletion"
	 */
	@Column
	protected boolean deleted = false;

	/**
	 * Returns a hash code calculated of all properties that are defined as unique (with the {@link Property} annotation). This is necessary to implement
	 * extendible combined unique constraints, regardless of which JPA inheritance strategy is used
	 */
	@Column(name = "uniquenessHash", unique = true, nullable = false)
	private Integer uniquenessHash = null;

	/**
	 * A value of -1 indicates that this is an unpersisted item.
	 * 
	 * @return the internal version of the Item, used for optimistic locking
	 */
	public int getVersion() {
		return version;
	}

	public Long getPk() {
		return pk;
	}

	@PrePersist
	public void prePersist() {
		this.createdAt = new Date();
		updateUniquenessHash();
	}

	/**
	 * Update uniqueness hash. This is the only column that has JPA unique-key constraint! This is necessary to make the {@link Column#unique()} annotation work
	 * with all JPA inheritance strategies.
	 */
	protected void updateUniquenessHash() {
		final Map<String, Object> uniqueProperties = getUniqueHashProperties();

		// check
		if (uniqueProperties.size() > 0) {
			final Collection<Object> uniquePropertyValues = uniqueProperties.values().stream().map(v -> {
				final Object prop;
				// transform all sub items into uniqueness hashes, use the real value of all
				// other properties
				if (v instanceof Item) {
					prop = ((Item) v).uniquenessHash();
				} else {
					prop = v;
				}

				return prop;
			}).collect(Collectors.toList());

			uniquePropertyValues.add(getTypeCode());

			// create a hashcode
			this.uniquenessHash = Objects.hash(uniquePropertyValues.toArray());
		} else {
			// use the PK as fallback, if no unique properties exist or all are null
			this.uniquenessHash = Objects.hash(this.pk);
		}
	}

	@PreUpdate
	protected void preUpdate() {
		setLastModifiedAt();
		updateUniquenessHash();
	}

	protected void setLastModifiedAt() {
		this.lastModifiedAt = new Date();
	}

	public int uniquenessHash() {
		return getUniqueHashProperties().hashCode();
	}

	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	public void setLastModifiedBy(final String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public LocalDateTime getLastModifiedAt() {
		return lastModifiedAt != null ? LocalDateTime.ofInstant(lastModifiedAt.toInstant(), ZoneId.systemDefault()) : null;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt != null ? LocalDateTime.ofInstant(createdAt.toInstant(), ZoneId.systemDefault()) : null;
	}

	/**
	 * @return true if the item has a PK. It is assumed that it has been saved before. If you set a PK manually and save the item, an existing item with the
	 *         same PK will be overwritten.
	 */
	public boolean isPersisted() {
		return pk != null;
	}

	/**
	 * Mark the object as dirty, even though it might no be.
	 */
	public void markAsDirty() {
		// auditingHandler.markModified(this);
		setLastModifiedAt();
	}

	/**
	 * Returns the names and the values of all properties annotated with @Unique.
	 * 
	 * @return a map containing all the unique properties for this Item (key = property name)
	 */
	public Map<String, Object> getUniqueHashProperties() {
		return getProperties(this::isUniqueField);
	}

	/**
	 * Returns all fields annotated with the {@link Property} annotation.
	 * 
	 * @param filter can be null or a predicate that further filters the returned item properties.
	 * @return all filtered item properties
	 */
	public Map<String, Object> getProperties(BiPredicate<Field, Object> filter) {
		if (this instanceof HibernateProxy) {
			if (!Hibernate.isInitialized(this)) {
				Hibernate.initialize(this);
			}
		}

		final Map<String, Object> props = new HashMap<>();

		for (final Field field : ClassUtil.getFieldsWithAnnotation(this.getClass(), Property.class)) {
			final Object propertyValue = ClassUtil.getField(this, field.getName(), true);

			if (filter == null || filter.test(field, propertyValue)) {
				props.put(field.getName(), propertyValue);
			}
		}

		return props;
	}
	
	/**
	 * Returns all fields annotated with the {@link Property} annotation.
	 * 
	 * @return all item properties
	 */
	public Map<String, Object> getProperties() {
		return getProperties(null);
	}

	/**
	 * Gets the property value for the given property name.
	 * 
	 * @param propertyName of the field to get the value from
	 * @return the field value
	 */
	public Object get(String propertyName) {
		return ClassUtil.getProperty(this, propertyName);
	}

	/**
	 * Sets the given property value. Unknown properties are ignored.
	 * 
	 * @param propertyName the name of the property to write to
	 * @param value        the property value
	 */
	public void set(String propertyName, Object value) {
		ClassUtil.setProperty(this, propertyName, value);
	}

	private boolean isUniqueField(Field field, Object fieldValue) {
		final Property prop = ClassUtil.getAnnotation(field, Property.class);

		return prop != null && prop.unique();
	}

	/**
	 * If the type and the pk of the given object is the same as the current object, both are equal.
	 *
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null || !(obj.getClass().equals(this.getClass()))) {
			return false;
		}

		if (pk == null) {
			return false;
		}

		return this.pk.equals(((Item) obj).pk);
	}

	@Override
	public int hashCode() {
		if (pk != null) {
			return pk.hashCode();
		} else {
			return super.hashCode();
		}
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

	public String getTypeCode() {
		final ItemType ann = ClassUtil.getAnnotation(this.getClass(), ItemType.class);
		return ann.typeCode();
	}

}
