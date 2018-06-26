package at.spot.core.persistence.query;

public abstract class AbstractQuery<T> {

	protected int page = 0;
	protected int pageSize = Integer.MAX_VALUE;
	protected Class<T> resultClass;
	protected int limit = 0;

	public AbstractQuery() {
		super();
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public Class<T> getResultClass() {
		return resultClass;
	}

	public void setResultClass(Class<T> resultClass) {
		this.resultClass = resultClass;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}