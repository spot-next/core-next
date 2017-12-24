package at.spot.core.persistence.service;

import at.spot.core.infrastructure.strategy.SerialNumberGeneratorStrategy;
import at.spot.core.persistence.exception.SerialNumberGeneratorException;
import at.spot.itemtype.core.UniqueIdItem;

/**
 * This service generates unique serial numbers for item instances of type
 * {@link UniqueIdItem}. The actual generation algorithm is implemented by
 * {@link SerialNumberGeneratorStrategy} instances registered for each item
 * type.
 */
public interface SerialNumberGeneratorService {

	/**
	 * * Generates a new serial number for the given iteme based on the last
	 * stored serial number.
	 * 
	 * @param item
	 *            for which a unique serial number should be generated
	 * @throws SerialNumberGeneratorException
	 *             if there is no SerialNumberGeneratorStrategy registered for
	 *             the given item type
	 */
	<T extends UniqueIdItem> void generate(T item) throws SerialNumberGeneratorException;

}
