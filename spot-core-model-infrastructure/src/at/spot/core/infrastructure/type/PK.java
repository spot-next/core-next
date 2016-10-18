package at.spot.core.infrastructure.type;

import java.math.BigInteger;

public class PK extends BigInteger {

	private static final long serialVersionUID = 1L;

	protected Class<?> type;

	public PK(long pk, Class<?> type) {
		super(String.valueOf(pk));
		this.type = type;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}
}
