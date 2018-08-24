package io.spotnext.core.infrastructure.http;

import java.util.ArrayList;
import java.util.List;

/**
 * This entity extends the spring ResponseEntity with the ability to set the
 * body and status after object creation.
 */
public class DataResponse extends AbstractResponse {

	final protected List<Status> errors = new ArrayList<>();
	final protected List<Status> warnings = new ArrayList<>();

	private DataResponse(HttpStatus httpStatus) {
		super(httpStatus);
	}

	public static  DataResponse ok() {
		return new DataResponse(HttpStatus.OK);
	}
	
	public static  DataResponse created() {
		return new DataResponse(HttpStatus.CREATED);
	}
	
	public static  DataResponse accepted() {
		return new DataResponse(HttpStatus.ACCEPTED);
	}

	public static  DataResponse notFound() {
		return new DataResponse(HttpStatus.NOT_FOUND);
	}
	
	public static  DataResponse conflict() {
		return new DataResponse(HttpStatus.CONFLICT);
	}

	public static  DataResponse internalServerError() {
		return new DataResponse(HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public static  DataResponse withStatus(HttpStatus httpStatus) {
		return new DataResponse(httpStatus);
	}

	public DataResponse withWarning(String code, String message) {
		warnings.add(new Status(code, message));
		return this;
	}
	
	public DataResponse withError(String code, String message) {
		errors.add(new Status(code, message));
		return this;
	}
}
