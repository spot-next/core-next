package io.spotnext.core.persistence.service;

import io.spotnext.core.persistence.exception.SequenceAccessException;

/**
 * This service generates unique serial numbers for the given sequence name.
 */
public interface SequenceGenerator {

	/**
	 * Retrieves the next sequence value for the given sequence.
	 * 
	 * @param sequenceName
	 *            the sequence used to retrieve the next value
	 */
	long getNextSequenceValue(String sequenceName) throws SequenceAccessException;

}
