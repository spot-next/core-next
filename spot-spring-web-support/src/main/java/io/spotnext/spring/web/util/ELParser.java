package io.spotnext.spring.web.util;

import org.apache.commons.collections4.Transformer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * <p>
 * ELParser class.
 * </p>
 *
 * @since 1.0
 * @author mojo2012
 * @version 1.0
 */
public class ELParser<I extends Object, O extends Object> implements Transformer<I, O> {
	private final String expression;

	/**
	 * @param expression to evaluate
	 */
	public ELParser(final String expression) {
		this.expression = expression;
	}

	/** {@inheritDoc} */
	@Override
	public O transform(final I object) {
		return evaluate(object, this.expression);
	}

	/**
	 * <p>
	 * evaluate.
	 * </p>
	 *
	 * @param          <I> the generic input type
	 * @param          <O> the generic output type
	 * @param object   the input value to evaluate
	 * @param property the property of the input value
	 * @return the value of the given property
	 */
	public static <I extends Object, O extends Object> O evaluate(final I object, final String property) {
		final ExpressionParser parser = new SpelExpressionParser();
		final Expression exp = parser.parseExpression(property);
		final EvaluationContext context = new StandardEvaluationContext(object);

		return (O) exp.getValue(context);
	}

}
