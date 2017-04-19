package at.spot.core.persistence.query;

public class Condition {
	
	
	public static Condition startsWith(String property, String value, boolean ignoreCase) {
		return null;
	}
	
	public static Condition notStartsWith(String property, String value, boolean ignoreCase) {
		return null;
	}
	
	public static Condition endsWith(String property, String value, boolean ignoreCase) {
		return null;
	}
	
	public static Condition notEndsWith(String property, String value, boolean ignoreCase) {
		return null;
	}
	
	public static Condition equals(String property, String value, boolean ignoreCase) {
		return null;
	}

	public static Condition notEquals(String property, String value, boolean ignoreCase) {
		return null;
	}

	public static Condition like(String property, String value, boolean ignoreCase) {
		return null;
	}

	public static Condition likeIgnoreCase(String property, String value, boolean ignoreCase) {
		return null;
	}

	public static Condition notLike(String property, String value, boolean ignoreCase) {
		return null;
	}
	
	public Condition and(Condition... conditions) {
		return null;
	}
	
	public Condition or(Condition... conditions) {
		return null;
	}
}
