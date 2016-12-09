package at.spot.core.infrastructure.type;

import java.io.Serializable;

import at.spot.core.model.Relation;

/**
 * Represents an item type property definition
 */
public class ItemTypePropertyRelationDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	public final RelationType relationType;
	public final RelationEndType relationEndType;
	public final Class<? extends Relation> relationItemType;

	public ItemTypePropertyRelationDefinition(final RelationType relationType, final RelationEndType relationEndType,
			final Class<? extends Relation> relationItemType) {
		this.relationType = relationType;
		this.relationEndType = relationEndType;
		this.relationItemType = relationItemType;
	}
}
