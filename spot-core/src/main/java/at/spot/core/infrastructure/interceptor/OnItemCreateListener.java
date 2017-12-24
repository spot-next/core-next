package at.spot.core.infrastructure.interceptor;

import at.spot.core.model.Item;

public interface OnItemCreateListener<T extends Item> extends ItemModificationListener<T> {

}
