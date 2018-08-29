package io.spotnext.core.persistence.query.lambda;

/**
 * Class designed to generate unique parameters for flexible serach expressions
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ParametersNameGenerator {
	private static final char FIRST_CHAR = 'a';
	private static final char LAST_CHAR = 'z';
	private static final char[] alphabet;

	static {
		alphabet = new char[LAST_CHAR - FIRST_CHAR + 1];
		for (char i = FIRST_CHAR; i <= LAST_CHAR; i++) {
			alphabet[i - FIRST_CHAR] = i;
		}
	}

	private int current;

	private StringBuilder alpha(int i) {
		final char r = alphabet[--i % alphabet.length];
		final int n = i / alphabet.length;
		return n == 0 ? new StringBuilder().append(r) : alpha(n).append(r);
	}

	/**
	 * Each call to this method returns next unique String (a, b, c, ..., z, aa, ab,
	 * ac,...)
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String next() {
		return alpha(++current).toString();
	}
}
