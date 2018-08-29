package io.spotnext.core.persistence.query.lambda;

import com.trigersoft.jaque.expression.ConstantExpression;
import com.trigersoft.jaque.expression.Expression;
import com.trigersoft.jaque.expression.ParameterExpression;
import com.trigersoft.jaque.expression.SimpleExpressionVisitor;

/**
 * Visitor which counts occurrences of given Expression types
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class CountingVisitor extends SimpleExpressionVisitor {

	private int constantExpressionsCount = 0;
	private int parameterExpressionCount = 0;

	/** {@inheritDoc} */
	@Override
	public Expression visit(final ConstantExpression e) {
		constantExpressionsCount++;
		return super.visit(e);
	}

	/** {@inheritDoc} */
	@Override
	public Expression visit(final ParameterExpression e) {
		parameterExpressionCount++;
		return super.visit(e);
	}

	/**
	 * <p>Getter for the field <code>constantExpressionsCount</code>.</p>
	 *
	 * @return a int.
	 */
	public int getConstantExpressionsCount() {
		return constantExpressionsCount;
	}

	/**
	 * <p>getParameterExpressionsCount.</p>
	 *
	 * @return a int.
	 */
	public int getParameterExpressionsCount() {
		return parameterExpressionCount;
	}
}
