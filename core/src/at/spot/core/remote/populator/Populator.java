package at.spot.core.remote.populator;


/**
 * Populates a target object with the properties from the given source object. 
 *
 * @param <S>
 * @param <T>
 */
public interface Populator<S, T> {
	void populate(S source, T target);
}
