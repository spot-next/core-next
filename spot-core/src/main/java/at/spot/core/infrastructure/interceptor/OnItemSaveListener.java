package at.spot.core.infrastructure.interceptor;

import at.spot.core.model.Item;

public interface OnItemSaveListener<T extends Item> extends ItemModificationListener<T> {

}
