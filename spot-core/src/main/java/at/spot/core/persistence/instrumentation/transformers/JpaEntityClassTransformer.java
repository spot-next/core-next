package at.spot.core.persistence.instrumentation.transformers;

import java.io.File;
import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.type.RelationNodeType;
import at.spot.core.infrastructure.type.RelationType;
import at.spot.instrumentation.ClassTransformer;
import at.spot.instrumentation.transformer.IllegalClassTransformationException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * Transforms custom {@link ItemType} annotations to JPA entity annotations.
 */
@ClassTransformer
public class JpaEntityClassTransformer extends AbstractBaseClassTransformer {

	protected static final String MV_CASCADE = "cascade";
	protected static final String MV_NODE_TYPE = "nodeType";
	protected static final String MV_REFERENCED_COLUMN_NAME = "referencedColumnName";
	protected static final String MV_PK = "pk";
	protected static final String MV_INVERSE_JOIN_COLUMNS = "inverseJoinColumns";
	protected static final String MV_JOIN_COLUMNS = "joinColumns";
	protected static final String MV_NAME = "name";
	protected static final String MV_RELATION_NAME = "relationName";
	protected static final String MV_PERSISTABLE = "persistable";
	protected static final String CLASS_FILE_SUFFIX = ".class";
	protected static final String MV_MAPPED_BY = "mappedBy";
	protected static final String MV_MAPPED_TO = "mappedTo";
	protected static final String MV_TYPE = "type";
	protected static final String MV_TYPE_CODE = "typeCode";
	protected static final String MV_UNIQUE = "unique";
	protected static final String MV_COLUMN_NAMES = "columnNames";
	protected static final String MV_UNIQUE_CONSTRAINTS = "uniqueConstraints";
	protected static final String RELATION_SOURCE_COLUMN = "source_pk";
	protected static final String RELATION_TARGET_COLUMN = "target_pk";

	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
	@Override
	protected Optional<CtClass> transform(final ClassLoader loader, final CtClass clazz,
			final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain)
			throws IllegalClassTransformationException {

		try {
			// we only want to transform item types only ...
			if (!clazz.isFrozen() && isItemType(clazz)) {

				// add JPA entity annotation
				addEntityAnnotation(clazz);

				// process item properties
				for (final CtField field : getDeclaredFields(clazz)) {
					if (!clazz.equals(field.getDeclaringClass())) {
						continue;
					}

					final Optional<Annotation> propertyAnn = getAnnotation(field, Property.class);

					// process item type property annotation
					if (propertyAnn.isPresent()) {
						// create the necessary JPA annotations based on Relation and Property
						// annotations
						final List<Annotation> fieldAnnotations = createJpaRelationAnnotations(clazz, field,
								propertyAnn.get());

						// only add column annotation if there is not relation annotation
						if (CollectionUtils.isEmpty(fieldAnnotations)) {
							// add column annotation used hold infos about unique constraints
							Optional<Annotation> columnAnn = createColumnAnnotation(clazz, field, propertyAnn.get());

							if (columnAnn.isPresent()) {
								fieldAnnotations.add(columnAnn.get());
							}
						}

						// and add them to the clazz
						addAnnotations(clazz, field, fieldAnnotations);
					}
				}

				try {
					final File file = new File("/var/tmp/" + clazz.getName() + CLASS_FILE_SUFFIX);

					if (file.exists()) {
						file.delete();
					}

					writeClass(clazz, file);
				} catch (final IOException e) {
					throw new IllegalClassTransformationException(
							String.format("Unable to write class file %s", clazz.getName()), e);
				}

				return Optional.of(clazz);
			}
		} catch (final Exception e) {
			throw new IllegalClassTransformationException(
					String.format("Unable process JPA annotations for class file %s", clazz.getName()), e);
		}

		return Optional.empty();
	}

	protected void addEntityAnnotation(final CtClass clazz) throws IllegalClassTransformationException {
		final Optional<Annotation> itemTypeAnn = getItemTypeAnnotation(clazz);

		if (itemTypeAnn.isPresent()) {
			final BooleanMemberValue val = (BooleanMemberValue) itemTypeAnn.get().getMemberValue(MV_PERSISTABLE);

			if (val != null && val.getValue()) {
				// this type needs a separate deployment table
				addAnnotations(clazz, Arrays.asList(createAnnotation(clazz, Entity.class)));
			} else {
				// this type is not an persistable entity
				addAnnotations(clazz, Arrays.asList(createAnnotation(clazz, MappedSuperclass.class)));
			}
		}
	}

	protected boolean isItemType(final CtClass clazz) throws IllegalClassTransformationException {
		if (!clazz.isFrozen()) {
			return getItemTypeAnnotation(clazz).isPresent();
		}

		return false;
	}

	protected Optional<Annotation> getItemTypeAnnotation(final CtClass clazz)
			throws IllegalClassTransformationException {

		return getAnnotation(clazz, ItemType.class);
	}

	protected String getItemTypeCode(final CtClass clazz) throws IllegalClassTransformationException {
		final Optional<Annotation> ann = getItemTypeAnnotation(clazz);

		if (ann.isPresent()) {
			final StringMemberValue typeCode = (StringMemberValue) ann.get().getMemberValue(MV_TYPE_CODE);

			return typeCode.getValue();
		}

		return null;
	}

	protected Optional<Annotation> createColumnAnnotation(final CtClass clazz, CtField field,
			final Annotation propertyAnnotation) {

		BooleanMemberValue val = (BooleanMemberValue) propertyAnnotation.getMemberValue(MV_UNIQUE);

		Annotation ann = null;

		if (val != null) {
			ann = createAnnotation(field.getFieldInfo2().getConstPool(), Column.class);
			ann.addMemberValue(MV_UNIQUE, val);
		}

		return Optional.ofNullable(ann);
	}

	protected List<Annotation> createJpaRelationAnnotations(final CtClass entityClass, final CtField field,
			final Annotation propertyAnnotation) throws NotFoundException, IllegalClassTransformationException {

		final List<Annotation> jpaAnnotations = new ArrayList<>();

		final Optional<Annotation> relAnnotation = getAnnotation(field, Relation.class);

		if (relAnnotation.isPresent()) {
			final EnumMemberValue relType = (EnumMemberValue) relAnnotation.get().getMemberValue(MV_TYPE);

			// JPA Relation annotations
			if (StringUtils.equals(relType.getValue(), RelationType.ManyToMany.toString())) {
				jpaAnnotations.add(createJpaRelationAnnotation(entityClass, field, ManyToMany.class));

				// JoinTable annotation for bi-directional m-to-n relation table
				jpaAnnotations
						.add(createJoinTableAnnotation(entityClass, field, propertyAnnotation, relAnnotation.get()));

			} else if (StringUtils.equals(relType.getValue(), RelationType.OneToMany.toString())) {
				final Annotation o2mAnn = createJpaRelationAnnotation(entityClass, field, OneToMany.class);
				addMappedByAnnotationValue(field, o2mAnn, entityClass, relAnnotation.get());
				jpaAnnotations.add(o2mAnn);

			} else if (StringUtils.equals(relType.getValue(), RelationType.ManyToOne.toString())) {
				jpaAnnotations.add(createJpaRelationAnnotation(entityClass, field, ManyToOne.class));

			} else {
				// one to one in case the field type is a subtype of Item

				jpaAnnotations.add(createJpaRelationAnnotation(entityClass, field, OneToOne.class));
			}

		} else if (isItemType(field.getType())) { // one to one in case the
													// field type is a subtype
													// of Item
			jpaAnnotations.add(createJpaRelationAnnotation(entityClass, field, OneToOne.class));
		} else if (hasInterface(field.getType(), Collection.class) || hasInterface(field.getType(), Map.class)) {
			jpaAnnotations.add(createAnnotation(entityClass, ElementCollection.class));
		}

		return jpaAnnotations;
	}

	protected void addMappedByAnnotationValue(CtField field, final Annotation annotation, final CtClass entityClass,
			final Annotation relation) {
		if (relation != null) {
			final StringMemberValue mappedTo = (StringMemberValue) relation.getMemberValue(MV_MAPPED_TO);

			annotation.addMemberValue(MV_MAPPED_BY,
					createAnnotationStringValue(field.getFieldInfo2().getConstPool(), mappedTo.getValue()));
		}
	}

	protected Annotation createJpaRelationAnnotation(final CtClass clazz, final CtField field,
			final Class<? extends java.lang.annotation.Annotation> annotationType)
			throws IllegalClassTransformationException {

		final Annotation ann = createAnnotation(clazz, annotationType);
		addCascadeAnnotation(ann, field);

		// add fetch type
		final EnumMemberValue fetchType = new EnumMemberValue(getConstPool(clazz));
		fetchType.setType(FetchType.class.getName());
		fetchType.setValue(FetchType.LAZY.name());
		ann.addMemberValue("fetch", fetchType);

		return ann;
	}

	protected Annotation createJoinTableAnnotation(final CtClass clazz, final CtField field,
			final Annotation propertyAnnotation, final Annotation relationAnnotation) {

		final StringMemberValue relationNameValue = (StringMemberValue) relationAnnotation
				.getMemberValue(MV_RELATION_NAME);

		// @JoinTable
		final Annotation joinTableAnn = createAnnotation(field.getFieldInfo2().getConstPool(), JoinTable.class);
		final StringMemberValue tableName = new StringMemberValue(field.getFieldInfo2().getConstPool());

		// generate relation table name
		tableName.setValue(relationNameValue.getValue());
		joinTableAnn.addMemberValue(MV_NAME, tableName);

		{// swap relationnode types according to the relation setting
			String joinColumnName = RELATION_SOURCE_COLUMN;
			String inverseJoinColumnName = RELATION_TARGET_COLUMN;

			final RelationNodeType nodeType = getRelationNodeType(relationAnnotation);

			if (RelationNodeType.TARGET.equals(nodeType)) {
				joinColumnName = RELATION_TARGET_COLUMN;
				inverseJoinColumnName = RELATION_SOURCE_COLUMN;
			}

			joinTableAnn.addMemberValue(MV_JOIN_COLUMNS, createJoinColumn(field, joinColumnName));
			joinTableAnn.addMemberValue(MV_INVERSE_JOIN_COLUMNS, createJoinColumn(field, inverseJoinColumnName));
		}

		return joinTableAnn;
	}

	protected ArrayMemberValue createJoinColumn(final CtField field, final String columnName) {
		final Annotation joinColumnAnn = createAnnotation(field.getFieldInfo2().getConstPool(), JoinColumn.class);

		final StringMemberValue column = new StringMemberValue(field.getFieldInfo2().getConstPool());
		column.setValue(MV_PK);
		joinColumnAnn.addMemberValue(MV_REFERENCED_COLUMN_NAME, column);

		final StringMemberValue name = new StringMemberValue(field.getFieldInfo2().getConstPool());
		name.setValue(columnName);
		joinColumnAnn.addMemberValue(MV_NAME, name);

		final AnnotationMemberValue val = new AnnotationMemberValue(field.getFieldInfo2().getConstPool());
		val.setValue(joinColumnAnn);

		return createAnnotationArrayValue(field.getFieldInfo2().getConstPool(), val);
	}

	protected RelationNodeType getRelationNodeType(final Annotation relationAnnotation) {
		final EnumMemberValue nodeType = (EnumMemberValue) relationAnnotation.getMemberValue(MV_NODE_TYPE);
		return RelationNodeType.valueOf(nodeType.getValue());
	}

	protected void addCascadeAnnotation(final Annotation annotation, final CtField field) {
		final EnumMemberValue val = new EnumMemberValue(field.getFieldInfo2().getConstPool());
		val.setType(CascadeType.class.getName());
		val.setValue(CascadeType.ALL.toString());

		annotation.addMemberValue(MV_CASCADE, createAnnotationArrayValue(field.getFieldInfo2().getConstPool(), val));
	}

}
