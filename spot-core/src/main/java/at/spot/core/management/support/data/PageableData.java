package at.spot.core.management.support.data;

import java.util.Collections;
import java.util.List;

public class PageableData<T> {
	List<T> objects = Collections.EMPTY_LIST;
	int page = 0;
	int pageSize = 0;
	int objectCount = 0;

	public PageableData(final List<T> objects, final int page, final int pageSize) {
		this.objects = Collections.unmodifiableList(objects);
		this.page = page;
		this.pageSize = pageSize;

		this.objectCount = objects.size();
	}

	public List<?> getObjects() {
		return objects;
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
