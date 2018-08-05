package io.spotnext.core.persistence.query.lambda;

import com.trigersoft.jaque.expression.ConstantExpression;
import com.trigersoft.jaque.expression.Expression;
import com.trigersoft.jaque.expression.ParameterExpression;
import com.trigersoft.jaque.expression.SimpleExpressionVisitor;

/**
 * Visitor which counts occurrences of given Expression types
 */
public class CountingVisitor extends SimpleExpressionVisitor {

	private int constantExpressionsCount = 0;
	private int parameterExpressionCount = 0;

	@Override
	public Expression visit(final ConstantExpression e) {
		constantExpressionsCount++;
		return super.visit(e);
	}

	@Override
	public Expression visit(final ParameterExpression e) {
		parameterExpressionCount++;
		return super.visit(e);
	}

	public int getConstantExpressionsCount() {
		return constantExpressionsCount;
	}

	public int getParameterExpressionsCount() {
		return parameterExpressionCount;
	}
}
