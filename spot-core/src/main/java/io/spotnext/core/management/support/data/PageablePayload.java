package io.spotnext.core.management.support.data;

import java.util.Collection;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.spotnext.core.persistence.query.QueryResult;

/**
 * This is a wrapper for result data that supports pagination.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@JsonInclude(Include.NON_NULL)
public class PageablePayload<T> extends QueryResult<T> {

	public PageablePayload(Collection<T> results, int page, int pageSize, Long totalCount) {
		super(results.stream().collect(Collectors.toList()), page, pageSize, totalCount);
	}

}
