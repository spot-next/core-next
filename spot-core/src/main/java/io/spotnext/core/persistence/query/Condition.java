package io.spotnext.core.persistence.query;

/**
 * <p>Condition class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class Condition {
	
	
	/**
	 * <p>startsWith.</p>
	 *
	 * @param property a {@link java.lang.String} object.
	 * @param value a {@link java.lang.String} object.
	 * @param ignoreCase a boolean.
	 * @return a {@link io.spotnext.core.persistence.query.Condition} object.
	 */
	public static Condition startsWith(String property, String value, boolean ignoreCase) {
		return null;
	}
	
	/**
	 * <p>notStartsWith.</p>
	 *
	 * @param property a {@link java.lang.String} object.
	 * @param value a {@link java.lang.String} object.
	 * @param ignoreCase a boolean.
	 * @return a {@link io.spotnext.core.persistence.query.Condition} object.
	 */
	public static Condition notStartsWith(String property, String value, boolean ignoreCase) {
		return null;
	}
	
	/**
	 * <p>endsWith.</p>
	 *
	 * @param property a {@link java.lang.String} object.
	 * @param value a {@link java.lang.String} object.
	 * @param ignoreCase a boolean.
	 * @return a {@link io.spotnext.core.persistence.query.Condition} object.
	 */
	public static Condition endsWith(String property, String value, boolean ignoreCase) {
		return null;
	}
	
	/**
	 * <p>notEndsWith.</p>
	 *
	 * @param property a {@link java.lang.String} object.
	 * @param value a {@link java.lang.String} object.
	 * @param ignoreCase a boolean.
	 * @return a {@link io.spotnext.core.persistence.query.Condition} object.
	 */
	public static Condition notEndsWith(String property, String value, boolean ignoreCase) {
		return null;
	}
	
	/**
	 * <p>equals.</p>
	 *
	 * @param property a {@link java.lang.String} object.
	 * @param value a {@link java.lang.String} object.
	 * @param ignoreCase a boolean.
	 * @return a {@link io.spotnext.core.persistence.query.Condition} object.
	 */
	public static Condition equals(String property, String value, boolean ignoreCase) {
		return null;
	}

	/**
	 * <p>notEquals.</p>
	 *
	 * @param property a {@link java.lang.String} object.
	 * @param value a {@link java.lang.String} object.
	 * @param ignoreCase a boolean.
	 * @return a {@link io.spotnext.core.persistence.query.Condition} object.
	 */
	public static Condition notEquals(String property, String value, boolean ignoreCase) {
		return null;
	}

	/**
	 * <p>like.</p>
	 *
	 * @param property a {@link java.lang.String} object.
	 * @param value a {@link java.lang.String} object.
	 * @param ignoreCase a boolean.
	 * @return a {@link io.spotnext.core.persistence.query.Condition} object.
	 */
	public static Condition like(String property, String value, boolean ignoreCase) {
		return null;
	}

	/**
	 * <p>likeIgnoreCase.</p>
	 *
	 * @param property a {@link java.lang.String} object.
	 * @param value a {@link java.lang.String} object.
	 * @param ignoreCase a boolean.
	 * @return a {@link io.spotnext.core.persistence.query.Condition} object.
	 */
	public static Condition likeIgnoreCase(String property, String value, boolean ignoreCase) {
		return null;
	}

	/**
	 * <p>notLike.</p>
	 *
	 * @param property a {@link java.lang.String} object.
	 * @param value a {@link java.lang.String} object.
	 * @param ignoreCase a boolean.
	 * @return a {@link io.spotnext.core.persistence.query.Condition} object.
	 */
	public static Condition notLike(String property, String value, boolean ignoreCase) {
		return null;
	}
	
	/**
	 * <p>and.</p>
	 *
	 * @param conditions a {@link io.spotnext.core.persistence.query.Condition} object.
	 * @return a {@link io.spotnext.core.persistence.query.Condition} object.
	 */
	public Condition and(Condition... conditions) {
		return null;
	}
	
	/**
	 * <p>or.</p>
	 *
	 * @param conditions a {@link io.spotnext.core.persistence.query.Condition} object.
	 * @return a {@link io.spotnext.core.persistence.query.Condition} object.
	 */
	public Condition or(Condition... conditions) {
		return null;
	}
}
