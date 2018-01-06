package at.spot.core.infrastructure.strategy;

import at.spot.core.model.Item;
import at.spot.core.persistence.exception.SerialNumberGeneratorException;
import at.spot.core.persistence.service.SerialNumberGeneratorService;

/**
 * Implementations of this strategy need to be registered with the
 * {@link SerialNumberGeneratorService} for each type they should be used with.
 * The strategy is the called with the base value and is expected to return a
 * fully generated serial number for the given item.
 */
public interface SerialNumberGeneratorStrategy<T extends Item> {

	/**
	 * Sets the generated id for the given item in case it has not been set manually
	 * before. The base serial number is provided from the persistence layer.
	 * 
	 * @param nextIdValue
	 *            the next serial number generated from the underlying persistence
	 *            layer
	 * @param item
	 *            for which the serial number is generated
	 * @throws SerialNumberGeneratorException
	 *             e.g. if there is some data missing needed to generate the serial
	 *             number. This should be prevented by proper validation though.
	 */
	void generate(long nextIdValue, T item) throws SerialNumberGeneratorException;

	/**
	 * Returns the item type class this strategy is handling.
	 */
	Class<T> getItemType();

}
