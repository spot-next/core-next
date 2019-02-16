package io.spotnext.infrastructure.type;

import java.io.Serializable;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.infrastructure.IndirectPropertyAccess;

@SuppressFBWarnings("EQ_DOESNT_OVERRIDE_EQUALS")
public class Bean implements Serializable, IndirectPropertyAccess {
	private static final long serialVersionUID = 1L;

	@Override
	public int hashCode() {
		final Object[] properties = getProperties().values().stream().toArray();

		return Objects.hash(properties);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !this.getClass().equals(obj.getClass())) {
			return false;
		}

		return getProperties().equals(((IndirectPropertyAccess) obj).getProperties());
	}

}
