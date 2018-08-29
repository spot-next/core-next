package io.spotnext.core.management.populator;


/**
 * Populates a target object with the properties from the given source object.
 *
 * @param <S>
 * @param <T>
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public interface Populator<S, T> {
	/**
	 * <p>populate.</p>
	 *
	 * @param source a S object.
	 * @param target a T object.
	 */
	void populate(S source, T target);
}
