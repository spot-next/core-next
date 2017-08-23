package at.spot.core.persistence.service;

import java.util.Map;

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
public interface SerialNumberGeneratorService {

	String TOKEN_SERIAL_NUMBER_ID = "{s}";
	String TOKEN_TYPE_TOKEN = "{typecode}";
	String KEY_SERIAL_NUMBER_GENERATOR = "persistence.serialnumbers." + TOKEN_TYPE_TOKEN;

	/**
	 * Generates a new serial number for the given type based on the last stored
	 * serial number. The args will be used to fill the tokens.
	 * 
	 * @param type
	 * @param args
	 * @return
	 */
	<T extends Item> String generate(Class<T> type, String... args);

	/**
	 * Generates a new serial number for the given type based on the last stored
	 * serial number. The args will be used to fill the tokens.
	 * 
	 * @param type
	 * @param args
	 * @return
	 */
	<T extends Item> String generate(Class<T> type, Map<String, String> args);

	/**
	 * Generates a new serial number for the given type based on the last stored
	 * serial number. The args will be used to fill the tokens.
	 * 
	 * @param type
	 *            needed to find the last used serial number
	 * @param template
	 *            the serial number template
	 * @param
	 * @return
	 */
	String generate(final String type, final String template, final Map<String, String> args);

	/**
	 * Resets the serial number generation for the given type.
	 * 
	 * @param type
	 */
	<T extends Item> void reset(Class<T> type);

	/**
	 * Resets the serial number generation for the given type.
	 * 
	 * @param type
	 */
	<T extends Item> void reset(String type);
}
