package at.spot.core.infrastructure.type;

import java.math.BigInteger;

import at.spot.core.data.model.Item;

public class PK extends BigInteger {

	private static final long serialVersionUID = 1L;

	protected Class<? extends Item> type;

	public PK(long pk, Class<? extends Item> type) {
		super(String.valueOf(pk));
		this.type = type;
	}

	public Class<? extends Item> getType() {
		return type;
	}

	public void setType(Class<? extends Item> type) {
		this.type = type;
	}
}
