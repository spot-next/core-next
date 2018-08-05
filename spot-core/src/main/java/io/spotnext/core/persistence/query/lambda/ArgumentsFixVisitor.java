package io.spotnext.core.persistence.query.lambda;

import java.util.List;

import com.trigersoft.jaque.expression.Expression;
import com.trigersoft.jaque.expression.InvocableExpression;
import com.trigersoft.jaque.expression.InvocationExpression;
import com.trigersoft.jaque.expression.ParameterExpression;
import com.trigersoft.jaque.expression.SimpleExpressionVisitor;

/**
 * Visitor which is designed to change InvocableExpression so they replace known
 * Parameters with constant expressions.
 *
 * For example if you have "str".equals("test") normally you would get
 * expression 'P0.equals(test)' buf ater
 * {@link #fixArguments(InvocableExpression, List)} the expression will be
 * 'str.equals(test)'
 */
public class ArgumentsFixVisitor extends SimpleExpressionVisitor {

	private final List<Expression> args;

	private ArgumentsFixVisitor(final List<Expression> args) {
		this.args = args;
	}

	public static InvocableExpression fixArguments(final InvocableExpression e, final List<Expression> args) {

		final ArgumentsFixVisitor parameterNormalizer = new ArgumentsFixVisitor(args);
		return (InvocableExpression) e.accept(parameterNormalizer);
	}

	@Override
	public Expression visit(final ParameterExpression e) {
		final int index = e.getIndex();
		if (index >= args.size())
			return e;
		final Expression x = args.get(index);
		if (x instanceof ParameterExpression && ((ParameterExpression) x).getIndex() == e.getIndex())
			return e;
		if (!e.getResultType().isAssignableFrom(x.getResultType())) {
			return e;
		}
		return x;
	}

	@Override
	public Expression visit(final InvocationExpression e) {
		final List<Expression> arguments = e.getArguments();
		final List<Expression> visitedArguments = visitExpressionList(arguments);
		if (visitedArguments != arguments) {
			return Expression.invoke(e.getTarget(), visitedArguments);
		}
		return e;
	}
}