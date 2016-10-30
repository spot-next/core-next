package at.spot.core.persistence.query;

public abstract class Operator {
	public static Operator and(Operator operators) {
		return null;
	}

	public static Operator and(Condition... conditions) {
		return null;
	}

	public static Operator or(Operator... operators) {
		return null;
	}

	public static Operator or(Condition... conditions) {
		return null;
	}
}
