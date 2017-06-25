package at.spot.spring.web.el;

import javax.el.BeanELResolver;
import javax.el.ELContext;
import javax.el.PropertyNotFoundException;

import org.springframework.expression.spel.SpelEvaluationException;

import at.spot.core.support.util.ELParser;

/**
 * Extends the default {@link BeanELResolver} implementation with support for
 * public fields. It first tries to find a proper getter method. As a fallback
 * it tries to read the corresponding public field.<br/>
 * It also handles unknown properties and getter methods gracefully and just
 * returns null.
 */
public class ExtendedBeanELResolver extends BeanELResolver {

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
