package io.spotnext.core.infrastructure.http;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This entity extends the spring ResponseEntity with the ability to set the
 * body and status after object creation.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
// hide super properties that are not used here
@JsonIgnoreProperties(value = { "model, viewName" })
public class DataResponse extends AbstractResponse {

	final protected List<Status> errors = new ArrayList<>();
	final protected List<Status> warnings = new ArrayList<>();

	protected DataResponse(HttpStatus httpStatus) {
		super(httpStatus);
	}

	/**
	 * <p>ok.</p>
	 *
	 * @return a {@link io.spotnext.infrastructure.http.DataResponse} object.
	 */
	public static DataResponse ok() {
		return new DataResponse(HttpStatus.OK);
	}

	/**
	 * <p>created.</p>
	 *
	 * @return a {@link io.spotnext.infrastructure.http.DataResponse} object.
	 */
	public static DataResponse created() {
		return new DataResponse(HttpStatus.CREATED);
	}

	/**
	 * <p>accepted.</p>
	 *
	 * @return a {@link io.spotnext.infrastructure.http.DataResponse} object.
	 */
	public static DataResponse accepted() {
		return new DataResponse(HttpStatus.ACCEPTED);
	}

	/**
	 * <p>notFound.</p>
	 *
	 * @return a {@link io.spotnext.infrastructure.http.DataResponse} object.
	 */
	public static DataResponse notFound() {
		return new DataResponse(HttpStatus.NOT_FOUND);
	}

	/**
	 * <p>conflict.</p>
	 *
	 * @return a {@link io.spotnext.infrastructure.http.DataResponse} object.
	 */
	public static DataResponse conflict() {
		return new DataResponse(HttpStatus.CONFLICT);
	}

	public static DataResponse badRequest() {
		return new DataResponse(HttpStatus.BAD_REQUEST);
	}

	/**
	 * <p>internalServerError.</p>
	 *
	 * @return a {@link io.spotnext.infrastructure.http.DataResponse} object.
	 */
	public static DataResponse internalServerError() {
		return new DataResponse(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * <p>withStatus.</p>
	 *
	 * @param httpStatus a {@link io.spotnext.infrastructure.http.HttpStatus} object.
	 * @return a {@link io.spotnext.infrastructure.http.DataResponse} object.
	 */
	public static DataResponse withStatus(HttpStatus httpStatus) {
		return new DataResponse(httpStatus);
	}

	/**
	 * <p>withWarning.</p>
	 *
	 * @param code a {@link java.lang.String} object.
	 * @param message a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.infrastructure.http.DataResponse} object.
	 */
	public DataResponse withWarning(String code, String message) {
		warnings.add(new Status(code, message));
		return this;
	}

	/**
	 * <p>withError.</p>
	 *
	 * @param code a {@link java.lang.String} object.
	 * @param message a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.infrastructure.http.DataResponse} object.
	 */
	public DataResponse withError(String code, String message) {
		errors.add(new Status(code, message));
		return this;
	}
}
