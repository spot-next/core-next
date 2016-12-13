package at.spot.core.infrastructure.type;

import java.io.Serializable;

import at.spot.core.model.Item;

/**
 * Represents an item type property definition
 */
public class ItemTypePropertyRelationDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	public final RelationType relationType;
	// public final RelationEndType relationEndType;
	// public final Class<? extends Relation> relationItemType;
	public final Class<? extends Item> referencedType;
	public final String mappedTo;

	public ItemTypePropertyRelationDefinition(final RelationType relationType,
			final Class<? extends Item> referencedType, final String mappedTo) {

		super();
		this.relationType = relationType;
		this.referencedType = referencedType;
		this.mappedTo = mappedTo;
	}
}
