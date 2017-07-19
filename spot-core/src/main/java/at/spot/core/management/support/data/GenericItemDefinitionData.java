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

	public String typeCode;
	public String typeName;
	public String typeClass;
	public String packageName;

	public List<Annotation> typeAnnotations = new ArrayList<>();

	public List<ItemTypePropertyDefinition> properties = new ArrayList<>();
	public List<Annotation> propertyAnnotations = new ArrayList<>();

	public void addProperty(ItemTypePropertyDefinition prop) {
		properties.add(prop);
	}

	public <A extends Annotation> void addPropertyAnnotation(A annotation) {
		propertyAnnotations.add(annotation);
	}

	public <A extends Annotation> void addTypeAnnotationProperty(A annotation) {
		typeAnnotations.add(annotation);
	}

}
