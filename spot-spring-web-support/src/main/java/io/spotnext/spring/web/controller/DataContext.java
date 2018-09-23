package io.spotnext.spring.web.controller;

import org.springframework.ui.ModelMap;

import io.spotnext.support.util.ClassUtil;

/**
 * <p>DataContext class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class DataContext extends ModelMap {
	private static final long serialVersionUID = 1L;

	private final AbstractData contextHolder;

	/**
	 * <p>Constructor for DataContext.</p>
	 *
	 * @param contextHolder a {@link io.spotnext.spring.web.controller.AbstractData} object.
	 */
	public DataContext(final AbstractData contextHolder) {
		this.contextHolder = contextHolder;
	}

	/** {@inheritDoc} */
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
