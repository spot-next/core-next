package io.spotnext.core.persistence.query.lambda;

import static com.trigersoft.jaque.expression.ExpressionType.Equal;
import static com.trigersoft.jaque.expression.ExpressionType.GreaterThan;
import static com.trigersoft.jaque.expression.ExpressionType.GreaterThanOrEqual;
import static com.trigersoft.jaque.expression.ExpressionType.IsNull;
import static com.trigersoft.jaque.expression.ExpressionType.LessThan;
import static com.trigersoft.jaque.expression.ExpressionType.LessThanOrEqual;
import static com.trigersoft.jaque.expression.ExpressionType.LogicalAnd;
import static com.trigersoft.jaque.expression.ExpressionType.LogicalOr;
import static com.trigersoft.jaque.expression.ExpressionType.NotEqual;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import com.trigersoft.jaque.expression.BinaryExpression;
import com.trigersoft.jaque.expression.ConstantExpression;
import com.trigersoft.jaque.expression.Expression;
import com.trigersoft.jaque.expression.ExpressionType;
import com.trigersoft.jaque.expression.ExpressionVisitor;
import com.trigersoft.jaque.expression.InvocableExpression;
import com.trigersoft.jaque.expression.InvocationExpression;
import com.trigersoft.jaque.expression.LambdaExpression;
import com.trigersoft.jaque.expression.MemberExpression;
import com.trigersoft.jaque.expression.ParameterExpression;
import com.trigersoft.jaque.expression.UnaryExpression;

import io.spotnext.core.infrastructure.annotation.Accessor;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.persistence.service.impl.DefaultLambdaQueryTranslationService;
import io.spotnext.core.types.Item;

/**
 * Visitor which translates Predicate lambda into
 * {@link io.spotnext.core.persistence.query.lambda.PredicateTranslationResult}
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ToJpqlQueryVisitor implements ExpressionVisitor<PredicateTranslationResult> {

	private static final String SQL_LIKE = "LIKE";
	private final PredicateTranslationResult sb = new PredicateTranslationResult();
	private final ParametersNameGenerator paramGenerator;
	private final Deque<UnaryOperator<Object>> parameterModifiers = new LinkedList<>();
	private boolean columnBlock = false;

	/**
	 * <p>Constructor for ToJpqlQueryVisitor.</p>
	 *
	 * @param paramGenerator a {@link io.spotnext.core.persistence.query.lambda.ParametersNameGenerator} object.
	 * @param modelService a {@link io.spotnext.core.infrastructure.service.ModelService} object.
	 */
	public ToJpqlQueryVisitor(final ParametersNameGenerator paramGenerator, final ModelService modelService) {
		this.paramGenerator = paramGenerator;
	}

	private String toSqlOp(final int expressionType) {
		switch (expressionType) {
		case LessThanOrEqual:
			return "<=";
		case GreaterThanOrEqual:
			return ">=";
		case LessThan:
			return "<";
		case GreaterThan:
			return ">";
		case Equal:
			return "=";
		case LogicalAnd:
			return "AND";
		case LogicalOr:
			return "OR";
		case IsNull:
			return "IS NULL";
		case NotEqual:
			return "<>";
		default:
			throw new UnsupportedOperationException(
					"unsupported expression type: " + expressionType + " " + ExpressionType.toString(expressionType));
		}
	}

	/** {@inheritDoc} */
	@Override
	public PredicateTranslationResult visit(final LambdaExpression<?> e) {
		final Expression body = e.getBody();
		return body.accept(this);
	}

	/** {@inheritDoc} */
	@Override
	public PredicateTranslationResult visit(final BinaryExpression e) {

		e.getFirst().accept(this);
		final int expressionType = e.getExpressionType();
		addSqlOperator(expressionType);
		e.getSecond().accept(this);

		return sb;
	}

	private void addSqlOperator(final int expressionType) {
		final String operator = toSqlOp(expressionType);
		addSqlOperator(operator);
	}

	private void addSqlOperator(final String operator) {
		sb.getWhere().append(" ").append(operator).append(" ");
	}

	/** {@inheritDoc} */
	@Override
	public PredicateTranslationResult visit(final ConstantExpression e) {
		final Object value = e.getValue();
		addSqlParam(value);
		return sb;
	}

	private void addSqlParam(final Object value) {
		final String paramName = paramGenerator.next();
		sb.getWhere().append(':').append(paramName);
		final Object param;
		if (!parameterModifiers.isEmpty()) {
			final UnaryOperator<Object> modifier = parameterModifiers.pop();
			param = modifier.apply(value);
		} else {
			param = value;
		}
		sb.getParameters().put(paramName, param);
	}

	/** {@inheritDoc} */
	@Override
	public PredicateTranslationResult visit(final InvocationExpression e) {
		final InvocableExpression fixedExpression = ArgumentsFixVisitor.fixArguments(e.getTarget(), e.getArguments());
		fixedExpression.accept(this);
		if (shouldExecuteParameters(fixedExpression)) {

			final List<Expression> arguments = filterArgumentExpressions(fixedExpression, e.getArguments());
			for (final Expression arg : arguments) {
				if (canBeExecuted(arg)) {
					final ToConstantExpressionVisitor visitor = new ToConstantExpressionVisitor();
					final ConstantExpression constantExpression = arg.accept(visitor);
					constantExpression.accept(this);
				} else {
					arg.accept(this);
				}
			}
		}

		// we have to also evaluate arguments
		return sb;
	}

	private List<Expression> filterArgumentExpressions(final InvocableExpression expression,
			final List<Expression> arguments) {
		if (getParametersCount(expression) == 0) {
			// if after arguments fix this expression has no parameters we have
			// to remove
			// all constant expression
			// from its parameters because they are already inlined
			return arguments.stream().filter(e -> !(e instanceof ConstantExpression)).collect(Collectors.toList());
		}
		return arguments;
	}

	private boolean shouldExecuteParameters(final InvocableExpression normalizedTarget) {
		return !(normalizedTarget instanceof LambdaExpression);
	}

	private int getParametersCount(final InvocableExpression expression) {
		return getCounts(expression).getParameterExpressionsCount();
	}

	private boolean canBeExecuted(final Expression arg) {
		final int expressionsCount = getExpressionsCount(arg);
		return expressionsCount > 0;
	}

	private int getExpressionsCount(final Expression arg) {
		return getCounts(arg).getConstantExpressionsCount();
	}

	private CountingVisitor getCounts(final Expression arg) {
		final CountingVisitor vistor = new CountingVisitor();
		arg.accept(vistor);
		return vistor;
	}

	/** {@inheritDoc} */
	@Override
	public PredicateTranslationResult visit(final ParameterExpression e) {
		return sb;
	}

	/** {@inheritDoc} */
	@Override
	public PredicateTranslationResult visit(final UnaryExpression e) {

		if (e.getExpressionType() == ExpressionType.IsNull) {
			e.getFirst().accept(this);
			addSqlOperator(e.getExpressionType());
			return sb;
		} else if (e.getExpressionType() == ExpressionType.Convert) {
			// if its cast then we don do operation (its probable Integer to int
			// or
			// something like that)
			return e.getFirst().accept(this);
		} else if (e.getExpressionType() == ExpressionType.LogicalNot) {
			// if negate -> negate whole statement
			sb.getWhere().append("NOT(");
			e.getFirst().accept(this);
			sb.getWhere().append(")");
			return sb;
		} else {
			addSqlOperator(e.getExpressionType());
			return e.getFirst().accept(this);
		}
	}

	private String getTableAlias(final MemberExpression e) {

		if (isFromRelation(e)) {
			final InvocationExpression instance = (InvocationExpression) e.getInstance();
			final MemberExpression parentMember = (MemberExpression) instance.getTarget();
			final String columnName = getColumnName(parentMember).get();
			return getTableAlias(parentMember) + columnName;
		}
		// by default normal
		return DefaultLambdaQueryTranslationService.FS_MAIN_ALIAS;
	}

	/** {@inheritDoc} */
	@Override
	public PredicateTranslationResult visit(final MemberExpression e) {

		final Optional<String> colName = getColumnName(e);
		final boolean isGetter = colName.isPresent();
		final boolean relation = isGetter && isFromRelation(e);

		final boolean oldColumnBlock = columnBlock;
		if (isGetter) {
			// if getter we only need the last getter so we block adding columns
			// on
			// recursive calls
			columnBlock = true;
		}

		e.getInstance().accept(this);
		if (relation) {
			final InvocationExpression instance = (InvocationExpression) e.getInstance();
			// this is getRelation
			final MemberExpression parentMember = (MemberExpression) instance.getTarget();
			final String columnName = getColumnName(parentMember).get();
			final String parentTable = getTableAlias(parentMember);
			final String table = getTableAlias(e);

			final String typeCode = instance.getResultType().getSimpleName();
			final String join = "LEFT JOIN " + typeCode + " as " + table + " on " + parentTable + "." + columnName
					+ " = " + table + ".PK ";
			sb.getJoins().add(join);
		}
		columnBlock = oldColumnBlock;

		if (isGetter) {
			if (!columnBlock) {
				addColumn(colName.get(), getTableAlias(e));

				if (isBoolean(e.getMember())) {
					// for boolean methods invocation we have to add '= true'
					addSqlOperator(Equal);
					addSqlParam(Boolean.TRUE);
				}
			}
		} else if (isEquals(e.getMember())) {
			// if method is equals we use '=' operator for sql
			addSqlOperator(ExpressionType.Equal);
		} else {
			tryHandleStringFunctions(e.getMember());
		}

		return sb;
	}

	private boolean tryHandleStringFunctions(final Member member) {
		if (member instanceof Method && member.getDeclaringClass().equals(String.class)
				&& ((Method) member).getParameters().length == 1) {
			final String methodName = member.getName();
			if (methodName.equals("startsWith")) {
				addSqlOperator(SQL_LIKE);
				parameterModifiers.push(e -> e + "%");
				return true;
			} else if (methodName.equals("endsWith")) {
				addSqlOperator(SQL_LIKE);
				parameterModifiers.push(e -> "%" + e);
				return true;
			} else if (methodName.equals("contains")) {
				addSqlOperator(SQL_LIKE);
				parameterModifiers.push(e -> "%" + e + "%");
				return true;
			}

		}
		return false;
	}

	private boolean isFromRelation(final MemberExpression e) {
		final Expression expression = e.getInstance();
		return expression instanceof InvocationExpression && Item.class.isAssignableFrom(expression.getResultType());
	}

	private void addColumn(final String columnName, final String tableAlias) {
		sb.getWhere().append(tableAlias).append(".").append(columnName);
	}

	private Optional<String> getColumnName(final MemberExpression ex) {

		// @formatter:off
		return Optional.of(ex.getMember()).filter(e -> e instanceof Method).map(e -> (Method) e)
				.map(e -> e.getAnnotation(Accessor.class)).map(Accessor::propertyName);
		// @formatter:on
	}

	private boolean isBoolean(final Member member) {
		return member instanceof Method && ((Method) member).getReturnType().equals(Boolean.class);
	}

	private boolean isEquals(final Member member) {
		if (member instanceof Method) {
			final Method method = (Method) member;
			if (method.getName().equals("equals") && method.getParameters().length == 1
					&& method.getParameters()[0].getType().equals(Object.class)) {
				return true;
			}
		}
		return false;
	}

}
