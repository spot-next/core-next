package io.spotnext.core.management.support.data;

import java.util.Collections;
import java.util.List;

/**
 * <p>PageablePayload class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class PageablePayload<T> {
	private final List<T> data;
	private final int page;
	private final int pageSize;
	private final int objectCount;

	/**
	 * <p>Constructor for PageablePayload.</p>
	 *
	 * @param objects a {@link java.util.List} object.
	 * @param page a int.
	 * @param pageSize a int.
	 */
	public PageablePayload(final List<T> objects, final int page, final int pageSize) {
		this.data = Collections.unmodifiableList(objects);
		this.page = page;
		this.pageSize = pageSize;
		this.objectCount = objects.size();
	}

	/**
	 * <p>Getter for the field <code>data</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<T> getData() {
		return data;
	}

	/**
	 * <p>Getter for the field <code>page</code>.</p>
	 *
	 * @return a int.
	 */
	public int getPage() {
		return page;
	}

	/**
	 * <p>Getter for the field <code>pageSize</code>.</p>
	 *
	 * @return a int.
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * <p>Getter for the field <code>objectCount</code>.</p>
	 *
	 * @return a int.
	 */
	public int getObjectCount() {
		return objectCount;
	}

}
