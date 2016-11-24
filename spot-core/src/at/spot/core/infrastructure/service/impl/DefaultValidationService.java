package at.spot.core.infrastructure.service.impl;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.ValidationService;

@Service
public class DefaultValidationService implements ValidationService {

	@Autowired
	protected Validator validator;

	@Override
	public <T extends Object> Set<ConstraintViolation<T>> validate(final T object) throws ValidationException {
		// final Errors errors = new BeanPropertyBindingResult(object,
		// object.getClass().getSimpleName());
		// validator.validate(object, errors);

		final Set<ConstraintViolation<T>> constraintViolations = validator.validate(object);

		return constraintViolations;
	}

}
