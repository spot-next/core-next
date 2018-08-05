package io.spotnext.maven.velocity.type.parts;

public class JavaField extends JavaMember {
	private static final long serialVersionUID = 1L;

	protected JavaExpression assignement;

	public JavaExpression getAssignement() {
		return assignement;
	}

	public void setAssignement(JavaExpression assignement) {
		this.assignement = assignement;
	}
}
