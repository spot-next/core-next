package at.spot.core.infrastructure.type;

import java.math.BigInteger;

import at.spot.core.model.Item;

public class PK extends BigInteger {

	protected Item type;
	
	public PK(long pk) {
		super(String.valueOf(pk));
	}

	public Item getType() {
		return type;
	}

	public void setType(Item type) {
		this.type = type;
	}
}
