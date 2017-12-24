package at.spot.core.infrastructure.interceptor;

import at.spot.core.model.Item;

public interface OnItemValidateListener<T extends Item> extends ItemModificationListener<T> {

}
