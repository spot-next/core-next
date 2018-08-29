package io.spotnext.core.infrastructure.support.impex;

import java.util.Locale;
import java.util.stream.Stream;

/**
 * <p>ImpexMergeMode class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public enum ImpexMergeMode {
	ADD("add"), APPEND("append"), REMOVE("remove"), REPLACE("replace");

	private String code;

	private ImpexMergeMode(String code) {
		this.code = code;
	}

	/**
	 * Returns the matching value for the given code.
	 *
	 * @param code will be transformed to lower case.
	 * @return null in case there is no matching value found.
	 */
	public static ImpexMergeMode forCode(String code) {
		String internalCode = code.toLowerCase(Locale.ENGLISH);
		return Stream.of(ImpexMergeMode.values()).filter(i -> i.code.equals(internalCode)).findFirst().orElse(null);
	}
}
