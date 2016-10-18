package at.spot.core.support.util;

import org.apache.commons.collections4.Transformer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

public class ELParser implements Transformer<Object, Object> {
	private String propertyName;

	public ELParser(String propertyName) {
		this.propertyName = propertyName;
	}

	@Override
	public Object transform(Object object) {
		ExpressionParser parser = new SpelExpressionParser();
		Expression exp = parser.parseExpression(this.propertyName);
		EvaluationContext context = new StandardEvaluationContext(object);

		return exp.getValue(context);
	}
}
