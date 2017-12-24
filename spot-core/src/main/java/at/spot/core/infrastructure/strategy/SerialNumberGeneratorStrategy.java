package at.spot.core.infrastructure.strategy;

import org.springframework.beans.factory.annotation.Required;

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
	 * 
	 * @param nextIdValue
	 *            the next serial number generated from the underlying
	 *            persistence layer
	 * @param item
	 *            for which the serial number is generated
	 * @return a fully generated serial number for the given item.
	 * @throws SerialNumberGeneratorException
	 *             e.g. if there is some data missing needed to generate the
	 *             serial number. This should be prevented by proper validation
	 *             though.
	 */
	void generate(long nextIdValue, T item) throws SerialNumberGeneratorException;

	/**
	 * Returns the item type class this strategy is handling.
	 */
	Class<T> getItemType();

	/**
	 * Sets the item type this strategy is responsible for.
	 */
	@Required
	void setItemType(Class<T> itemType);
}
