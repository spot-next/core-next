package io.spotnext.core.persistence.query.lambda;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.trigersoft.jaque.expression.BinaryExpression;
import com.trigersoft.jaque.expression.ConstantExpression;
import com.trigersoft.jaque.expression.DelegateExpression;
import com.trigersoft.jaque.expression.Expression;
import com.trigersoft.jaque.expression.ExpressionVisitor;
import com.trigersoft.jaque.expression.InvocationExpression;
import com.trigersoft.jaque.expression.LambdaExpression;
import com.trigersoft.jaque.expression.MemberExpression;
import com.trigersoft.jaque.expression.ParameterExpression;
import com.trigersoft.jaque.expression.UnaryExpression;

/**
 * Visitor that tries to execute given Expression and returns
 * {@link com.trigersoft.jaque.expression.ConstantExpression}
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ToConstantExpressionVisitor implements ExpressionVisitor<ConstantExpression> {

	private List<Expression> arguments = Collections.emptyList();

	/** {@inheritDoc} */
	@Override
	public ConstantExpression visit(final BinaryExpression binaryExpression) {
		throw new UnsupportedOperationException("Binary expression unsupported " + binaryExpression);
	}

	/** {@inheritDoc} */
	@Override
	public ConstantExpression visit(final ConstantExpression constantExpression) {
		return constantExpression;
	}

	/** {@inheritDoc} */
	@Override
	public ConstantExpression visit(final InvocationExpression invocationExpression) {
		final List<Expression> oldArgument = arguments;
		arguments = invocationExpression.getArguments().stream().map(e -> e.accept(this)).collect(Collectors.toList());
		final ConstantExpression result = invocationExpression.getTarget().accept(this);
		this.arguments = oldArgument;
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public ConstantExpression visit(final LambdaExpression<?> lambdaExpression) {
		return lambdaExpression.getBody().accept(this);
	}

	/** {@inheritDoc} */
	@Override
	public ConstantExpression visit(final MemberExpression memberExpression) {

		final Member member = memberExpression.getMember();
		if (member instanceof Method) {
			final List<Expression> oldParams = arguments;
			final ConstantExpression obj = memberExpression.getInstance().accept(this);
			arguments = oldParams;
			final Object[] args = memberExpression.getParameters().stream().map(ParameterExpression::getIndex)
					.map(e -> arguments.get(e)).map(e -> e.accept(this).getValue()).toArray();
			try {

				final Object result = ((Method) member).invoke(obj.getValue(), args);
				return Expression.constant(result);
			} catch (final ReflectiveOperationException e) {
				throw new IllegalStateException("cant do reflection call", e);
			}
		}

		throw new UnsupportedOperationException("member unsupported" + member);
	}

	/** {@inheritDoc} */
	@Override
	public ConstantExpression visit(final ParameterExpression parameterExpression) {
		final Expression params = arguments.get(parameterExpression.getIndex());
		return params.accept(this);
	}

	/** {@inheritDoc} */
	@Override
	public ConstantExpression visit(final UnaryExpression unaryExpression) {
		throw new UnsupportedOperationException("unaryExpression unsupported" + unaryExpression);
	}

	@Override
	public ConstantExpression visit(DelegateExpression delegateExpression) {
		throw new UnsupportedOperationException("delegateExpression unsupported" + delegateExpression);
	}

}
