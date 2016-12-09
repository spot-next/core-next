package at.spot.core.infrastructure.annotation;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import at.spot.core.infrastructure.type.RelationEndType;
import at.spot.core.infrastructure.type.RelationType;
import at.spot.core.model.OneToManyRelation;

@Target({ FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Relation {
	RelationType type();

	/**
	 * The default {@link OneToManyRelation} or {@link ManyToOneRelation} item
	 * types can be used. If many relation items are created, this might cause a
	 * performance drop. Therefore it is possible to create a dedicated item
	 * type that will be stored separately for performance improvements.
	 * 
	 * @return
	 */
	Class<? extends at.spot.core.model.Relation> relationItemType();

	RelationEndType endType();

	String referenceProperty();
}
