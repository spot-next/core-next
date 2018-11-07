package io.spotnext.core.management.support.data;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.infrastructure.type.ItemTypePropertyDefinition;

/**
 * This is a generic item type DTO implementation.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@SuppressFBWarnings("URF_UNREAD_PUBLIC_OR_PROTECTED_FIELD")
public class GenericItemDefinitionData {

	protected String typeCode;
	protected String typeName;
	protected String typeClass;
	protected String packageName;
	protected boolean isAbstract;

	protected List<Annotation> typeAnnotations = new ArrayList<>();

	protected final List<ItemTypePropertyDefinition> properties = new ArrayList<>();
	protected final List<Annotation> propertyAnnotations = new ArrayList<>();

	/**
	 * <p>
	 * addProperty.
	 * </p>
	 *
	 * @param prop a {@link io.spotnext.infrastructure.type.support.ItemTypePropertyDefinition} object.
	 */
	public void addProperty(ItemTypePropertyDefinition prop) {
		properties.add(prop);
	}

	/**
	 * <p>
	 * addPropertyAnnotation.
	 * </p>
	 *
	 * @param annotation a A object.
	 * @param            <A> a A object.
	 */
	public <A extends Annotation> void addPropertyAnnotation(A annotation) {
		propertyAnnotations.add(annotation);
	}

	/**
	 * <p>
	 * addTypeAnnotationProperty.
	 * </p>
	 *
	 * @param annotation a A object.
	 * @param            <A> a A object.
	 */
	public <A extends Annotation> void addTypeAnnotationProperty(A annotation) {
		typeAnnotations.add(annotation);
	}

	/**
	 * <p>
	 * Getter for the field <code>typeCode</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTypeCode() {
		return typeCode;
	}

	/**
	 * <p>
	 * Setter for the field <code>typeCode</code>.
	 * </p>
	 *
	 * @param typeCode a {@link java.lang.String} object.
	 */
	public void setTypeCode(String typeCode) {
		this.typeCode = typeCode;
	}

	/**
	 * <p>
	 * Getter for the field <code>typeName</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * <p>
	 * Setter for the field <code>typeName</code>.
	 * </p>
	 *
	 * @param typeName a {@link java.lang.String} object.
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * <p>
	 * Getter for the field <code>typeClass</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTypeClass() {
		return typeClass;
	}

	/**
	 * <p>
	 * Setter for the field <code>typeClass</code>.
	 * </p>
	 *
	 * @param typeClass a {@link java.lang.String} object.
	 */
	public void setTypeClass(String typeClass) {
		this.typeClass = typeClass;
	}

	/**
	 * <p>
	 * Getter for the field <code>packageName</code>.
	 * </p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * <p>
	 * Setter for the field <code>packageName</code>.
	 * </p>
	 *
	 * @param packageName a {@link java.lang.String} object.
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	/**
	 * <p>
	 * Getter for the field <code>typeAnnotations</code>.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<Annotation> getTypeAnnotations() {
		return typeAnnotations;
	}

	/**
	 * <p>
	 * Setter for the field <code>typeAnnotations</code>.
	 * </p>
	 *
	 * @param typeAnnotations a {@link java.util.List} object.
	 */
	public void setTypeAnnotations(List<Annotation> typeAnnotations) {
		this.typeAnnotations = typeAnnotations;
	}

	/**
	 * <p>
	 * Getter for the field <code>properties</code>.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<ItemTypePropertyDefinition> getProperties() {
		return properties;
	}

	/**
	 * <p>
	 * Getter for the field <code>propertyAnnotations</code>.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<Annotation> getPropertyAnnotations() {
		return propertyAnnotations;
	}

	/**
	 * Specifies if the item type is abstact
	 * 
	 * @return
	 */
	public boolean isAbstract() {
		return isAbstract;
	}

	/**
	 * Specifies if the item type is abstact
	 * 
	 * @param isAbstract
	 */
	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

}
