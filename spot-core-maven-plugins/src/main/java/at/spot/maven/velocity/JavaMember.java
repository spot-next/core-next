package at.spot.maven.velocity;

public abstract class JavaMember extends AbstractJavaObject {
	private static final long serialVersionUID = 1L;

	protected AbstractComplexJavaType complexType;
	protected JavaPrimitiveType primitiveType;

	public AbstractComplexJavaType getComplexType() {
		return complexType;
	}

	public JavaPrimitiveType getPrimitiveType() {
		return primitiveType;
	}

	public void setComplexType(AbstractComplexJavaType complexType) {
		this.primitiveType = null;
		this.complexType = complexType;
	}

	public void setPrimitiveType(JavaPrimitiveType primitiveType) {
		this.complexType = null;
		this.primitiveType = primitiveType;
	}

	public boolean isComplexType() {
		return complexType != null;
	}
}
