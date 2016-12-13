package at.spot.core.infrastructure.annotation;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import at.spot.core.infrastructure.type.RelationType;
import at.spot.core.model.Item;
import at.spot.core.model.OneToManyRelation;

@Target({ FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Relation {
	RelationType type();

	/**
	 * If {@link OneToManyRelation} is used, the {@link Relation#mappedTo()}
	 * property is a single property containing the item reference of the other
	 * end. {@link ManyToOneRelation} basically means the same, just that the
	 * referencing property is a collection.
	 */
	Class<? extends Item> referencedType();

	/**
	 * This is the name of the property of the referenced item.
	 */
	String mappedTo();

	/**
	 * If this is "true", deleting a reference from a relation collection also
	 * deletes the real item.<br />
	 * Example: When deleting a user from a user group and this is set to
	 * "true", the referenced user is also deleted. If set to "false" the
	 * referenced user is kept, only the reference is removed on both sides of
	 * the relation. <br />
	 */
	boolean casacadeOnDelete() default false;
}
