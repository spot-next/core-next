package io.spotnext.core.persistence.service;

import io.spotnext.core.persistence.exception.SequenceAccessException;

/**
 * This service generates unique serial numbers for the given sequence name.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface SequenceGenerator {

	/**
	 * Retrieves the next sequence value for the given sequence.
	 *
	 * @param sequenceName
	 *            the sequence used to retrieve the next value
	 * @return a long.
	 * @throws io.spotnext.core.persistence.exception.SequenceAccessException if any.
	 */
	long getNextSequenceValue(String sequenceName) throws SequenceAccessException;

}
