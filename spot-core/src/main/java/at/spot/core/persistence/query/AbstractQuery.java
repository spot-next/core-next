package at.spot.core.persistence.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractQuery<T> {

	protected final Class<T> resultClass;
	protected int page = 0;
	protected int pageSize = Integer.MAX_VALUE;
	protected int limit = 0;
	protected final List<String> fetchSubGraphs = new ArrayList<>();
	protected boolean fetchAllSubGrahps = false;

	public AbstractQuery(Class<T> resultClass) {
		this.resultClass = resultClass;
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

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public List<String> getFetchSubGraphs() {
		return fetchSubGraphs;
	}

	public void setFetchSubGraphs(String... fetchSubGraphs) {
		if (fetchSubGraphs != null) {
			this.fetchSubGraphs.addAll(Arrays.asList(fetchSubGraphs));
		}
	}

	public boolean isFetchAllSubGrahps() {
		return fetchAllSubGrahps;
	}

	/**
	 * Enable this to eagerly laod all subgraphs (item references) in one query.
	 * This can reduce stress on the database, although it increases memory usage as
	 * all data is loaded at once.
	 */
	public void setFetchAllSubGrahps(boolean fetchAllSubGrahps) {
		this.fetchAllSubGrahps = fetchAllSubGrahps;
	}

	public void setFetchSubGraphs(List<String> fetchSubGraphs) {
		this.fetchSubGraphs.addAll(fetchSubGraphs);
	}

}