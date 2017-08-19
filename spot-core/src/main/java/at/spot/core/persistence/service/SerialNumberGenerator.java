package at.spot.core.persistence.service;

import at.spot.core.constant.CoreConstants;
import at.spot.core.model.Item;

/**
 * This service generates unique serial numbers for item types. The number
 * template has to be configured in the spring properties like this:<br>
 * <br>
 * <b>{@value CoreConstants#KEY_SERIAL_NUMBER_GENERATOR}={pattern}</b> <br>
 * <br>
 * The pattern can contain following symbols:<br>
 * <ul>
 * <li>{s}: the serial number sequence
 * <li>{0}-{x}: the index of the given arguments
 * </ul>
 */
public interface SerialNumberGenerator {

	String TOKEN_SERIAL_NUMBER_ID = "{s}";
	String TOKEN_TYPE_TOKEN = "{typecode}";
	String KEY_SERIAL_NUMBER_GENERATOR = "persistence.serialnumbers." + TOKEN_TYPE_TOKEN;

	/**
	 * 
	 * @param type
	 * @param args
	 * @return
	 */
	<T extends Item> String generate(Class<T> type, String... args);

}
