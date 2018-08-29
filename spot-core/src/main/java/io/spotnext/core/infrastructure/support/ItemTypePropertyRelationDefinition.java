package io.spotnext.core.infrastructure.support;

import java.io.Serializable;

import io.spotnext.core.infrastructure.type.RelationType;

/**
 * Represents an item type property definition
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ItemTypePropertyRelationDefinition implements Serializable {
	private static final long serialVersionUID = 1L;

	public final RelationType relationType;
	// public final RelationEndType relationEndType;
	// public final Class<? extends Relation> relationItemType;
	public final String mappedTo;

	/**
	 * <p>Constructor for ItemTypePropertyRelationDefinition.</p>
	 *
	 * @param relationType a {@link io.spotnext.core.infrastructure.type.RelationType} object.
	 * @param mappedTo a {@link java.lang.String} object.
	 */
	public ItemTypePropertyRelationDefinition(final RelationType relationType, final String mappedTo) {

		super();
		this.relationType = relationType;
		this.mappedTo = mappedTo;
	}
}
