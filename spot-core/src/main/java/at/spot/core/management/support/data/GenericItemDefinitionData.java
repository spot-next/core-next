package at.spot.core.management.support.data;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import at.spot.core.infrastructure.support.ItemTypePropertyDefinition;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This is a generic item type DTO implementation.
 */
@SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
public class GenericItemDefinitionData {

	protected String typeCode;
	protected String typeName;
	protected String typeClass;
	protected String packageName;

	protected List<Annotation> typeAnnotations = new ArrayList<>();

	protected final List<ItemTypePropertyDefinition> properties = new ArrayList<>();
	protected final List<Annotation> propertyAnnotations = new ArrayList<>();

	public void addProperty(ItemTypePropertyDefinition prop) {
		properties.add(prop);
	}

	public <A extends Annotation> void addPropertyAnnotation(A annotation) {
		propertyAnnotations.add(annotation);
	}

	public <A extends Annotation> void addTypeAnnotationProperty(A annotation) {
		typeAnnotations.add(annotation);
	}

	public String getTypeCode() {
		return typeCode;
	}

	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeClass() {
		return typeClass;
	}

	public void setTypeClass(String typeClass) {
		this.typeClass = typeClass;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public List<Annotation> getTypeAnnotations() {
		return typeAnnotations;
	}

	public void setTypeAnnotations(List<Annotation> typeAnnotations) {
		this.typeAnnotations = typeAnnotations;
	}

	public List<ItemTypePropertyDefinition> getProperties() {
		return properties;
	}

	public List<Annotation> getPropertyAnnotations() {
		return propertyAnnotations;
	}
}
