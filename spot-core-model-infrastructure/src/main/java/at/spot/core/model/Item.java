package at.spot.core.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.VersionStrategy;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.collections4.comparators.NullComparator;
import org.datanucleus.api.jdo.annotations.CreateTimestamp;
import org.hibernate.Hibernate;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.zoodb.api.impl.ZooPC;

import at.spot.core.infrastructure.IdGenerator;
import at.spot.core.infrastructure.annotation.Property;

//JDO
@PersistenceCapable
@javax.jdo.annotations.Inheritance(strategy = InheritanceStrategy.SUPERCLASS_TABLE)
@Discriminator(strategy = DiscriminatorStrategy.CLASS_NAME)
@javax.jdo.annotations.Version(strategy = VersionStrategy.VERSION_NUMBER)
// JPA
@MappedSuperclass
// @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@EntityListeners({ AuditingEntityListener.class })
public abstract class Item extends ZooPC implements Serializable, Comparable<Item> {

	private static final long serialVersionUID = 1L;

	// @Resource
	// protected AuditingHandler auditingHandler;

	// JDO
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.NATIVE)
	// JPA
	@Id
	final protected Long pk = IdGenerator.createLongId();

	@Version
	protected Long version;

	@Transient
	protected String typeCode;

	@Property
	@org.datanucleus.api.jdo.annotations.UpdateTimestamp
	@UpdateTimestamp
	@LastModifiedDate
	protected Date lastModifiedAt;

	@Property
	@CreateTimestamp
	@CreationTimestamp
	@CreatedDate
	protected Date createdAt;

	public Long getPk() {
		return pk;
	}

	protected Long getVersion() {
		return this.version;
	}

	public Date getLastModifiedAt() {
		return lastModifiedAt != null ? new Date(lastModifiedAt.getTime()) : null;
	}

	public Date getCreatedAt() {
		return createdAt != null ? new Date(createdAt.getTime()) : null;
	}

	/**
	 * @return true if the item has a PK. It is assumed that it has been saved
	 *         before. If you set a PK manually and save the item, an existing
	 *         item with the same PK will be overwritten.
	 */
	public boolean isPersisted() {
		return getVersion() != null;
	}

	/**
	 * Mark the object as dirty, even though it might no be.
	 */
	public void markAsDirty() {
		// auditingHandler.markModified(this);
		this.lastModifiedAt = new Date();
	}

	protected <T extends Item> void addRelationMapping(final Collection<T> localRelationProperty,
			final T referencingItem) {
		localRelationProperty.add(referencingItem);
	}

	protected <T extends Item> void removeRelationMapping(final Collection<T> localRelationProperty,
			final T referencingItem) {
		localRelationProperty.remove(referencingItem);
	}

	/**
	 * If the type and the pk of the given object is the same as the current
	 * object, both are equal.
	 *
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof Item)) {
			return false;
		}

		// use this to circumvent failing checks caused by proxied objects
		if (Hibernate.getClass(this) != Hibernate.getClass(obj)) {
			return false;
		}

		return this.getPk().equals(((Item) obj).getPk());
	}

	@Override
	public int hashCode() {
		return getPk().hashCode();
	}

	@Override
	public int compareTo(final Item obj) {
		if (equals(obj)) {
			return 0;
		}

		return Objects.compare(this.getPk(), obj.getPk(), new NullComparator<Long>());
	}
}
