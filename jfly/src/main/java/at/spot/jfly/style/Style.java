package at.spot.jfly.style;

import io.gsonfire.annotations.ExposeMethodResult;

/**
 * The base interface for component style enums.
 */
public interface Style {
	/**
	 * Returns the value of the style as string.
	 */
	@ExposeMethodResult(value = "internal")
	public String internalName();

}
