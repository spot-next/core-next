package io.spotnext.core.infrastructure.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.spotnext.core.persistence.query.QueryResult;

/**
 * Jackson mixing configuration for {@link QueryResult}.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
@JsonPropertyOrder(value = { "page", "pageSie", "objectCount", "data" })
public abstract class QueryResultMixIn {

}
