package io.spotnext.core.infrastructure.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.core.infrastructure.service.ValidationService;

/**
 * <p>DefaultValidationService class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class DefaultValidationService implements ValidationService {

	@Autowired
	protected Validator validator;
	
	/** {@inheritDoc} */
	@Override
	public <T extends Object> Set<ConstraintViolation<T>> validate(final T object) throws ValidationException {
		// final Errors errors = new BeanPropertyBindingResult(object,
		// object.getClass().getSimpleName());
		// validator.validate(object, errors);

		final T unproxifiedItem;
		
		// TODO: check if dirty before unproxying?
		if (object instanceof HibernateProxy) {
			unproxifiedItem = (T) Hibernate.unproxy(object);
		} else {
			unproxifiedItem = object;
		}
		
		final Set<ConstraintViolation<T>> constraintViolations = validator.validate(unproxifiedItem);

		return constraintViolations;
	}

	/** {@inheritDoc} */
	@Override
	public String convertToReadableMessage(final Set<ConstraintViolation<?>> violations) {
		final ConstraintViolation<?> violation = violations.iterator().next();

		final String message = violations.stream()
				.map(v -> String.format("%s.%s %s", violation.getRootBeanClass().getSimpleName(),
						violation.getPropertyPath().toString(), violation.getMessage()))
				.distinct().collect(Collectors.joining(", "));
		return message;
	}

}
