package io.spotnext.core.persistence.service;

import io.spotnext.infrastructure.SequenceAccessException;

/**
 * This service generates unique serial numbers for the given sequence name.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface SequenceGenerator {

	/**
	 * Retrieves (and increments) the next sequence value for the given sequence.
	 *
	 * @param sequenceName the sequence used to retrieve the next value
	 * @return the generated sequence id
	 * @throws SequenceAccessException in case there was an exception during generation of the sequence id
	 */
	long getNextSequenceValue(String sequenceName) throws SequenceAccessException;

	/**
	 * @param sequenceName the sequence used to retrieve the next value
	 * @return the sequence value that will be returned next time the {@link #getNextSequenceValue(String)} will be called.
	 */
	long getCurrentSequenceValue(String sequenceName);

}
