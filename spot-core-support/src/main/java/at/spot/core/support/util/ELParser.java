package at.spot.core.support.util;

import java.util.Map;

import org.apache.commons.collections4.Transformer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class ELParser implements Transformer<Object, Object> {
	private final String propertyName;

	public ELParser(final String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public Object transform(final Object object) {
		final ExpressionParser parser = new SpelExpressionParser();
		final Expression exp = parser.parseExpression(this.propertyName);
		final EvaluationContext context = new StandardEvaluationContext(object);

		return exp.getValue(context);
	}

	public static String parseExpression(final String expression, final Map<String, Object> contextObjects) {
		// final ExpressionParser parser = new SpelExpressionParser();
		// Expression exp = parser.parseExpression(expression);
		//
		// final EvaluationContext context = new StandardEvaluationContext();
		//
		// for (final String var : contextObjects.keySet()) {
		// context.setVariable(var, contextObjects.get(var));
		// }
		//
		// return exp.getva

		return null;
	}
}
