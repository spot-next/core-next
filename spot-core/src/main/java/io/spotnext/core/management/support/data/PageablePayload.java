package io.spotnext.core.management.support.data;

import java.util.Collections;
import java.util.List;

public class PageablePayload<T> {
	private final List<T> data;
	private final int page;
	private final int pageSize;
	private final int objectCount;

	public PageablePayload(final List<T> objects, final int page, final int pageSize) {
		this.data = Collections.unmodifiableList(objects);
		this.page = page;
		this.pageSize = pageSize;
		this.objectCount = objects.size();
	}

	public List<T> getData() {
		return data;
	}

	public int getPage() {
		return page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getObjectCount() {
		return objectCount;
	}

}
