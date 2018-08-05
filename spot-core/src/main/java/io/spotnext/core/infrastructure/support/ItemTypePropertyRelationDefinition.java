package io.spotnext.core.infrastructure.support;

import java.io.Serializable;

import io.spotnext.core.infrastructure.type.RelationType;

/**
 * Represents an item type property definition
 */
public class ItemTypePropertyRelationDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	public final RelationType relationType;
	// public final RelationEndType relationEndType;
	// public final Class<? extends Relation> relationItemType;
	public final String mappedTo;

	public ItemTypePropertyRelationDefinition(final RelationType relationType, final String mappedTo) {

		super();
		this.relationType = relationType;
		this.mappedTo = mappedTo;
	}
}
