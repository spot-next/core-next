package at.spot.core.types;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import org.apache.commons.collections4.comparators.NullComparator;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import at.spot.core.infrastructure.IdGenerator;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.support.util.ClassUtil;

// JPA
@MappedSuperclass
// @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EntityListeners({ AuditingEntityListener.class })
public abstract class Item implements Serializable, Comparable<Item> {

	private static final long serialVersionUID = 1L;
	public static final String TYPECODE = "item";
	public static final String PROPERTY_PK = "pk";
	public static final String PROPERTY_LAST_MODIFIED_AT = "lastModifiedAt";
	public static final String PROPERTY_CREATED_AT = "createdAt";

	// @Resource
	// protected AuditingHandler auditingHandler;

	// JPA
	@Id
	@Column(name = "pk")
	final protected Long pk = IdGenerator.createLongId();

	@CreationTimestamp
	@CreatedDate
	protected Date createdAt;

	@CreatedBy
	protected String createdBy;

	@UpdateTimestamp
	@LastModifiedDate
	protected Date lastModifiedAt;

	@LastModifiedBy
	protected String lastModifiedBy;

	@Version
	protected long version = -1;

	/**
	 * Indicates that the given item is deleted. This property can be used to
	 * implemented "soft-deletion"
	 */
	@Column
	protected boolean deleted = false;

	/**
	 * Returns a hash code calculated of all properties that are defined as unique
	 * (with the {@link Property} annotation). This is necessary to implement
	 * extendible combined unique constraints, regardless of which JPA inheritance
	 * strategy is used
	 */
	@Column(name = "uniquenessHash", unique = true, nullable = false)
	private Integer uniquenessHash = null;

	/**
	 * A value of -1 indicates that this is an unpersisted item.
	 */
	public long getVersion() {
		return version;
	}

	public Long getPk() {
		return pk;
	}

	@PrePersist
	public void prePersist() {
		this.createdAt = new Date();
		uptedateUniquenessHash();
	}

	protected void uptedateUniquenessHash() {
		// update uniqueness hash. This is the only column that has unique-key
		// constraint!

		Collection<Object> uniquePropertyValues = getUniqueProperties().values();

		if (uniquePropertyValues.size() > 0) {
			this.uniquenessHash = Objects.hash(uniquePropertyValues.toArray());
		} else {
			this.uniquenessHash = Objects.hash(this.pk);
		}
	}

	@PreUpdate
	protected void preUpdate() {
		setLastModifiedAt();
		uptedateUniquenessHash();
	}

	protected void setLastModifiedAt() {
		this.lastModifiedAt = new Date();
	}

	public int uniquenessHash() {
		return getUniqueProperties().hashCode();
	}

	public void setCreatedBy(final String createdBy) {
		this.createdBy = createdBy;
	}

	public void setLastModifiedBy(final String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public Date getLastModifiedAt() {
		// return lastModifiedAt != null ? new Date(lastModifiedAt.getTime()) :
		// null;
		return lastModifiedAt;
	}

	public Date getCreatedAt() {
		// return createdAt != null ? new Date(createdAt.getTime()) : null;
		return createdAt;
	}

	/**
	 * @return true if the item has a PK. It is assumed that it has been saved
	 *         before. If you set a PK manually and save the item, an existing item
	 *         with the same PK will be overwritten.
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
	 * If the type and the pk of the given object is the same as the current object,
	 * both are equal.
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
