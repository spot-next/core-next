package at.spot.core.infrastructure.interceptor;

import at.spot.core.model.Item;

public interface OnItemLoadListener<T extends Item> extends ItemModificationListener<T> {

}
