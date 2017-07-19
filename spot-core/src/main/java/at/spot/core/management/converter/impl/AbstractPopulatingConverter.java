package at.spot.core.management.converter.impl;

import java.util.List;

import at.spot.core.management.converter.Converter;
import at.spot.core.management.populator.Populator;

public class AbstractPopulatingConverter<S, T> implements Converter<S, T> {

	protected List<Populator<S, T>> populators;
	protected Class<T> targetClass;

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

	public void setTargetClass(final Class<T> targetClass) {
		this.targetClass = targetClass;
	}

	public void setPopulators(final List<Populator<S, T>> populators) {
		this.populators = populators;
	}

	public List<Populator<S, T>> getPopulators() {
		return populators;
	}

	public Class<T> getTargetClass() {
		return targetClass;
	}
}
