package io.spotnext.core.infrastructure.interceptor.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.exception.ItemInterceptorException;
import io.spotnext.core.infrastructure.interceptor.ItemPrepareInterceptor;
import io.spotnext.core.persistence.exception.SequenceAccessException;
import io.spotnext.core.persistence.service.SequenceGenerator;
import io.spotnext.itemtype.core.UniqueIdItem;

/**
 * Generates a unique value for the {@link io.spotnext.itemtype.core.UniqueIdItem#getUid()} property if it
 * is not already set.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class UniqueIdItemSaveInterceptor extends AbstractItemInterceptor<UniqueIdItem>
		implements ItemPrepareInterceptor<UniqueIdItem> {

	@Autowired
	protected SequenceGenerator sequenceGenerator;

	/** {@inheritDoc} */
	@Override
	public Class<UniqueIdItem> getItemType() {
		return UniqueIdItem.class;
	}

	/** {@inheritDoc} */
	@Override
	public void onPrepare(final UniqueIdItem item) throws ItemInterceptorException {
		try {
			// only get a new sequence id if the id property is empty to save
			// some ids
			if (item.getUid() == null) {
				final String typeCode = typeService.getTypeCodeForClass(item.getClass());
				final long nextVal = sequenceGenerator.getNextSequenceValue(typeCode + "_" + "id");

				item.setUid(typeCode + "-" + nextVal);
			}
		} catch (final SequenceAccessException e) {
			throw new ItemInterceptorException("Could not generate unique id for item.", e);
		}
	}
}