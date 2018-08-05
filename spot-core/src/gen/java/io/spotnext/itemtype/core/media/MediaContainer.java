/**
 * This file is auto-generated. All changes will be overwritten.
 */
package io.spotnext.itemtype.core.media;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.annotation.ItemType;
import io.spotnext.core.infrastructure.annotation.Property;
import io.spotnext.core.infrastructure.annotation.Relation;
import io.spotnext.core.infrastructure.support.ItemCollectionFactory;

import io.spotnext.itemtype.core.CatalogItem;
import io.spotnext.itemtype.core.media.Media;

import java.io.Serializable;

import java.lang.String;

import java.util.Set;


@SuppressWarnings("unchecked")
@SuppressFBWarnings({"MF_CLASS_MASKS_FIELD",
    "EI_EXPOSE_REP",
    "EI_EXPOSE_REP2"
})
@ItemType(persistable = true, typeCode = "mediacontainer")
public class MediaContainer extends CatalogItem {
    /** Default serialVersionUID value. */
    private static final long serialVersionUID = 1L;
    public static final String TYPECODE = "mediacontainer";
    public static final String PROPERTY_MEDIAS = "medias";
    @Property(readable = true, writable = true)
    @Relation(collectionType = io.spotnext.core.infrastructure.type.RelationCollectionType.Set, relationName = "MediaContainer2Media", mappedTo = "container", type = io.spotnext.core.infrastructure.type.RelationType.OneToMany, nodeType = io.spotnext.core.infrastructure.type.RelationNodeType.SOURCE)
    public Set<Media> medias;

    @Accessor(propertyName = "medias", type = io.spotnext.core.infrastructure.type.AccessorType.get)
    public Set<Media> getMedias() {
        return ItemCollectionFactory.wrap(this, "medias", this.medias);
    }

    @Accessor(propertyName = "medias", type = io.spotnext.core.infrastructure.type.AccessorType.set)
    public void setMedias(Set<Media> medias) {
        this.medias = medias;
    }
}
