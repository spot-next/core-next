package io.spotnext.core.management.support.data;

import java.util.Collections;
import java.util.List;

import io.spotnext.core.infrastructure.http.Payload;

public class PageablePayload<T> extends Payload<List<T>> {
	final int page;
	final int pageSize;
	final int objectCount;

	public PageablePayload(final List<T> objects, final int page, final int pageSize) {
		super(Collections.unmodifiableList(objects));
		this.page = page;
		this.pageSize = pageSize;
		this.objectCount = objects.size();
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
