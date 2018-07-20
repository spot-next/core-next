package at.spot.maven.velocity.type.parts;

public class JavaField extends JavaMember {
	private static final long serialVersionUID = 1L;

	protected Expression assignement;

	public Expression getAssignement() {
		return assignement;
	}

	public void setAssignement(Expression assignement) {
		this.assignement = assignement;
	}
}
