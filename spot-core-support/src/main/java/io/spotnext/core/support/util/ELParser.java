package io.spotnext.core.support.util;

import org.apache.commons.collections4.Transformer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class ELParser<I extends Object, O extends Object> implements Transformer<I, O> {
	private final String expression;

	public ELParser(final String expression) {
		this.expression = expression;
	}

	@Override
	public O transform(final I object) {
		return evaluate(object, this.expression);
	}

	public static <I extends Object, O extends Object> O evaluate(final I object, final String property) {
		final ExpressionParser parser = new SpelExpressionParser();
		final Expression exp = parser.parseExpression(property);
		final EvaluationContext context = new StandardEvaluationContext(object);

		return (O) exp.getValue(context);
	}

}
