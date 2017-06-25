package at.spot.core.management.converter.impl;

import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import at.spot.core.management.populator.Populator;

@Component
public class GenericPopulatingConverter<S, T> implements Converter<S, T> {

	protected List<Populator<S, T>> populators;
	protected Class<T> targetClass;

	@Override
	public T convert(S source) {
		T target = createFromClass();

		for (Populator<S, T> p : getPopulators()) {
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

	public void setTargetClass(Class<T> targetClass) {
		this.targetClass = targetClass;
	}

	public void setPopulators(List<Populator<S, T>> populators) {
		this.populators = populators;
	}

	public List<Populator<S, T>> getPopulators() {
		return populators;
	}

	public Class<T> getTargetClass() {
		return targetClass;
	}
}
