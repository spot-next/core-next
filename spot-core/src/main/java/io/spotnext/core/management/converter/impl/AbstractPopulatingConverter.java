package io.spotnext.core.management.converter.impl;

import java.util.List;

import io.spotnext.core.management.converter.Converter;
import io.spotnext.core.management.populator.Populator;

/**
 * <p>AbstractPopulatingConverter class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class AbstractPopulatingConverter<S, T> implements Converter<S, T> {

	protected List<Populator<S, T>> populators;
	protected Class<T> targetClass;

	/** {@inheritDoc} */
	@Override
	public T convert(final S source) {
		final T target = createFromClass();

		for (final Populator<S, T> p : getPopulators()) {
			p.populate(source, target);
		}

		return target;
	}

	protected T createFromClass() {
		try {
			return getTargetClass().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * <p>Setter for the field <code>targetClass</code>.</p>
	 *
	 * @param targetClass a {@link java.lang.Class} object.
	 */
	public void setTargetClass(final Class<T> targetClass) {
		this.targetClass = targetClass;
	}

	/**
	 * <p>Setter for the field <code>populators</code>.</p>
	 *
	 * @param populators a {@link java.util.List} object.
	 */
	public void setPopulators(final List<Populator<S, T>> populators) {
		this.populators = populators;
	}

	/**
	 * <p>Getter for the field <code>populators</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<Populator<S, T>> getPopulators() {
		return populators;
	}

	/**
	 * <p>Getter for the field <code>targetClass</code>.</p>
	 *
	 * @return a {@link java.lang.Class} object.
	 */
	public Class<T> getTargetClass() {
		return targetClass;
	}
}
