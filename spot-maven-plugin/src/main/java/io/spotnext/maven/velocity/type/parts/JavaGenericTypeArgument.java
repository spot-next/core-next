package io.spotnext.maven.velocity.type.parts;

import java.io.Serializable;

public class JavaGenericTypeArgument implements Serializable {
	private static final long serialVersionUID = 1L;

	protected JavaMemberType type;
	protected boolean isWildCard = false;

	public JavaGenericTypeArgument(JavaMemberType type, boolean isWildCard) {
		this.isWildCard = isWildCard;
		this.type = type;
	}

	public JavaMemberType getType() {
		return type;
	}

	public void setType(JavaMemberType type) {
		this.type = type;
	}

	public boolean isWildCard() {
		return isWildCard;
	}

	public void setWildCard(boolean isWildCard) {
		this.isWildCard = isWildCard;
	}
}
