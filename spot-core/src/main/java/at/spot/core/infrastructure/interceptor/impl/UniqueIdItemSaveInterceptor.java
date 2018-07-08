package at.spot.core.infrastructure.interceptor.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.exception.ItemInterceptorException;
import at.spot.core.infrastructure.interceptor.ItemPrepareInterceptor;
import at.spot.core.persistence.exception.SequenceAccessException;
import at.spot.core.persistence.service.SequenceGenerator;
import at.spot.itemtype.core.UniqueIdItem;

/**
 * Generates a unique value for the {@link UniqueIdItem#getId()} property if it
 * is not already set.
 */
@Service
public class UniqueIdItemSaveInterceptor extends AbstractItemInterceptor<UniqueIdItem>
		implements ItemPrepareInterceptor<UniqueIdItem> {

	@Resource
	protected SequenceGenerator sequenceGenerator;

	@Override
	public Class<UniqueIdItem> getItemType() {
		return UniqueIdItem.class;
	}

	@Override
	public void onPrepare(final UniqueIdItem item) throws ItemInterceptorException {

		try {
			// only get a new sequence id if the id property is empty to save
			// some ids
			if (item.getId() == null) {
				final String typeCode = typeService.getTypeCodeForClass(item.getClass());
				final long nextVal = sequenceGenerator.getNextSequenceValue(typeCode + "_" + "id");

				item.setId(typeCode + "-" + nextVal);
			}
		} catch (final SequenceAccessException e) {
			throw new ItemInterceptorException("Could not generate unique id for item.", e);
		}
	}

}
