package io.spotnext.core.infrastructure.interceptor.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import io.spotnext.core.infrastructure.interceptor.ItemCreateInterceptor;
import io.spotnext.core.infrastructure.interceptor.ItemInterceptor;
import io.spotnext.core.infrastructure.interceptor.ItemLoadInterceptor;
import io.spotnext.core.infrastructure.interceptor.ItemPrepareInterceptor;
import io.spotnext.core.infrastructure.interceptor.ItemRemoveInterceptor;
import io.spotnext.core.infrastructure.interceptor.ItemValidateInterceptor;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.infrastructure.support.ItemInterceptorRegistry;
import io.spotnext.infrastructure.type.Item;

/**
 * This is the base class for all {@link io.spotnext.infrastructure.interceptor.ItemInterceptor} implementations. It
 * handles the registration of the interceptor with the appropriate
 * {@link io.spotnext.infrastructure.support.ItemInterceptorRegistry}.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractItemInterceptor<T extends Item> extends AbstractService implements ItemInterceptor<T> {

	@Resource
	protected TypeService typeService;

	@Resource
	protected ItemInterceptorRegistry<ItemCreateInterceptor<Item>> itemCreateInterceptorRegistry;

	@Resource
	protected ItemInterceptorRegistry<ItemValidateInterceptor<Item>> itemValidateInterceptorRegistry;

	@Resource
	protected ItemInterceptorRegistry<ItemPrepareInterceptor<Item>> itemPrepareInterceptorRegistry;

	@Resource
	protected ItemInterceptorRegistry<ItemLoadInterceptor<Item>> itemLoadInterceptorRegistry;

	@Resource
	protected ItemInterceptorRegistry<ItemRemoveInterceptor<Item>> itemRemoveInterceptorRegistry;

	/**
	 * Registers the current item interceptor with the appropriate registry.
	 */
	@PostConstruct
	public void setup() {
		String typeCode = typeService.getTypeCodeForClass(getItemType());

		if (this instanceof ItemCreateInterceptor) {
			itemCreateInterceptorRegistry.registerMapping(typeCode, (ItemCreateInterceptor<Item>) this);
		} else if (this instanceof ItemValidateInterceptor) {
			itemValidateInterceptorRegistry.registerMapping(typeCode, (ItemValidateInterceptor<Item>) this);
		} else if (this instanceof ItemPrepareInterceptor) {
			itemPrepareInterceptorRegistry.registerMapping(typeCode, (ItemPrepareInterceptor<Item>) this);
		} else if (this instanceof ItemLoadInterceptor) {
			itemLoadInterceptorRegistry.registerMapping(typeCode, (ItemLoadInterceptor<Item>) this);
		} else if (this instanceof ItemRemoveInterceptor) {
			itemRemoveInterceptorRegistry.registerMapping(typeCode, (ItemRemoveInterceptor<Item>) this);
		}
	}
}
