package io.spotnext.core.infrastructure.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.spotnext.core.infrastructure.http.ModelAndView;

/**
 * Jackson mixing configuration for {@link ModelAndView}.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
@JsonIgnoreProperties({ "model", "viewName" })
@JsonPropertyOrder(value = { "httpStatus", "warnings", "errors", "payload", "data" })
public abstract class ModelAndViewMixIn {

	@JsonIgnore
	public abstract Object getModel();
	
	@JsonIgnore
	public abstract String getViewName();
}
