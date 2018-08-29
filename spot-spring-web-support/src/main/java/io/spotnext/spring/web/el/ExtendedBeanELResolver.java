package io.spotnext.spring.web.el;

import javax.el.BeanELResolver;
import javax.el.ELContext;
import javax.el.PropertyNotFoundException;

import org.springframework.expression.spel.SpelEvaluationException;

import io.spotnext.core.support.util.ELParser;

/**
 * Extends the default {@link javax.el.BeanELResolver} implementation with
 * support for public fields. It first tries to find a proper getter method. As
 * a fallback it tries to read the corresponding public field. It also handles
 * unknown properties and getter methods gracefully and just returns null.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ExtendedBeanELResolver extends BeanELResolver {

	/** {@inheritDoc} */
	@Override
	public Object getValue(final ELContext context, final Object base, final Object property) {
		try {
			return super.getValue(context, base, property);
		} catch (final PropertyNotFoundException e) {
			Object value = null;

			try {
				final ELParser<Object, Object> parser = new ELParser<>((String) property);
				value = parser.transform(base);
				context.setPropertyResolved(true);
			} catch (final SpelEvaluationException e2) {
				// silently ignore errors
			}

			return value;
		}
	}
}
