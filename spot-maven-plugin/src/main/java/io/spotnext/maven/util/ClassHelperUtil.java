package io.spotnext.maven.util;

/**
 * <p>ClassHelperUtil class.</p>
 *
 * @since 1.0
 */
public class ClassHelperUtil {
	/**
	 * <p>getClassName.</p>
	 *
	 * @param fullyQualifiedClassName a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getClassName(final String fullyQualifiedClassName) {
		return fullyQualifiedClassName.substring(getClassNameStartIndex(fullyQualifiedClassName) + 1);
	}

	/**
	 * <p>getClassPackage.</p>
	 *
	 * @param fullyQualifiedClassName a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getClassPackage(final String fullyQualifiedClassName) {
		return fullyQualifiedClassName.substring(0, getClassNameStartIndex(fullyQualifiedClassName));
	}

	/**
	 * <p>getClassNameStartIndex.</p>
	 *
	 * @param fullyQualifiedClassName a {@link java.lang.String} object.
	 * @return a int.
	 */
	protected static int getClassNameStartIndex(final String fullyQualifiedClassName) {
		return fullyQualifiedClassName.lastIndexOf(".");
	}
}
