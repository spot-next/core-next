package at.spot.maven.util;

public class MiscUtil {
	public static String getClassName(final String fullyQualifiedClassName) {
		return fullyQualifiedClassName.substring(getClassNameStartIndex(fullyQualifiedClassName) + 1);
	}

	public static String getClassPackage(final String fullyQualifiedClassName) {
		return fullyQualifiedClassName.substring(0, getClassNameStartIndex(fullyQualifiedClassName));
	}

	protected static int getClassNameStartIndex(final String fullyQualifiedClassName) {
		return fullyQualifiedClassName.lastIndexOf(".");
	}
}
