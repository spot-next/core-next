package at.spot.core.infrastructure.annotation;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import at.spot.core.infrastructure.type.RelationCollectionType;
import at.spot.core.infrastructure.type.RelationNodeType;
import at.spot.core.infrastructure.type.RelationType;

@Target({ FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Relation {
	boolean DEFAULT_CASCADE_ON_DELETE = false;

	RelationType type();

	/**
	 * If {@link OneToManyRelation} is used, the {@link Relation#mappedTo()}
	 * property is a single property containing the item reference of the other end.
	 * {@link ManyToOneRelation} basically means the same, just that the referencing
	 * property is a collection.
	 */
	// Class<? extends Item> referencedType();

	/**
	 * The name of the relation. Depending on the underlying persistence framework,
	 * this might be used for the relation table name.
	 */
	String relationName() default "";

	RelationCollectionType collectionType() default RelationCollectionType.List;

	/**
	 * This is the name of the property of the referenced item.
	 */
	String mappedTo() default "";

	/**
	 * Defines at which end of the relation the current object is.
	 */
	RelationNodeType nodeType();

	/**
	 * If this is "true", deleting a reference from a relation collection also
	 * deletes the real item.<br />
	 * Example: When deleting a user from a user group and this is set to "true",
	 * the referenced user is also deleted. If set to "false" the referenced user is
	 * kept, only the reference is removed on both sides of the relation. <br />
	 */
	// boolean casacadeOnDelete() default DEFAULT_CASCADE_ON_DELETE;
}
