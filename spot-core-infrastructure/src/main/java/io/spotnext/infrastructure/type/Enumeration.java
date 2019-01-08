package io.spotnext.infrastructure.type;

import java.util.List;

/**
 * This is just a marker interface for generated enums.
 */
public interface Enumeration {
	String getInternalName();

	List<Enumeration> getValues();
}
