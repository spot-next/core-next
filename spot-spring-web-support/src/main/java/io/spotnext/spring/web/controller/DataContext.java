package io.spotnext.spring.web.controller;

import org.springframework.ui.ModelMap;

import io.spotnext.core.support.util.ClassUtil;

public class DataContext extends ModelMap {
	private static final long serialVersionUID = 1L;

	private final AbstractData contextHolder;

	public DataContext(final AbstractData contextHolder) {
		this.contextHolder = contextHolder;
	}

	@Override
	public Object get(final Object key) {
		if (key instanceof String) {
			final Object ret = getFromContextHolder((String) key);

			if (ret != null) {
				return ret;
			}
		}

		return super.get(key);
	}

	protected Object getFromContextHolder(final String key) {
		return ClassUtil.getField(contextHolder, key, true);
	}
}
