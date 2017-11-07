package at.spot.core.persistence.instrumentation.transformers;

import java.io.File;
import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

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
import javassist.bytecode.annotation.MemberValue;
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

		// we only want to transform item types only ...
		if (!clazz.isFrozen() && isItemType(clazz)) {

			// add JPA entity annotation
			addEntityAnnotation(clazz);
			addUniqueConstraints(clazz);

			// process item properties
			for (final CtField field : getDeclaredFields(clazz)) {
				if (!clazz.equals(field.getDeclaringClass())) {
					continue;
				}

				final Optional<Annotation> propertyAnn = getAnnotation(field, Property.class);

				// process item type property annotation
				if (propertyAnn.isPresent()) {
					final Optional<Annotation> relationAnn = getAnnotation(field, Relation.class);

					// create the necessary JPA annotations based on Relation
					// and Property
					// annotations
					try {
						final List<Annotation> jpaAnnotations = createJpaRelationAnnotations(clazz, field,
								propertyAnn.get(), relationAnn.orElse(null));

						// and add them to the clazz
						addAnnotations(clazz, field, jpaAnnotations);
					} catch (final Exception e) {
						throw new IllegalClassTransformationException(
								String.format("Unable process JPA annotations for class file %s", clazz.getName()), e);
					}
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

		return Optional.empty();
	}

	protected void addEntityAnnotation(final CtClass clazz) {
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

	protected void addUniqueConstraints(final CtClass clazz) {
		final Map<String, CtField> uniqueFields = new HashMap<>();

		for (final CtField field : clazz.getFields()) {
			final Optional<Annotation> propertyAnn = getAnnotation(field, Property.class);

			if (propertyAnn.isPresent()) {
				final BooleanMemberValue val = (BooleanMemberValue) propertyAnn.get().getMemberValue(MV_UNIQUE);

				if (val != null && val.getValue()) {
					uniqueFields.put(field.getName(), field);
				}
			}
		}

		if (!uniqueFields.isEmpty()) {
			final Annotation tableAnn = createAnnotation(getConstPool(clazz), Table.class);

			final ArrayMemberValue constraints = new ArrayMemberValue(getConstPool(clazz));
			tableAnn.addMemberValue(MV_UNIQUE_CONSTRAINTS, constraints);

			final Annotation constraintAnnotation = createAnnotation(getConstPool(clazz), UniqueConstraint.class);
			final AnnotationMemberValue constraint = new AnnotationMemberValue(getConstPool(clazz));
			constraint.setValue(constraintAnnotation);
			constraints.setValue(new MemberValue[] { constraint });

			final ArrayMemberValue columnNames = new ArrayMemberValue(getConstPool(clazz));
			constraintAnnotation.addMemberValue(MV_COLUMN_NAMES, columnNames);

			final List<StringMemberValue> columnNameValues = new ArrayList<>();

			StringMemberValue cv = null;

			for (final Map.Entry<String, CtField> uniqueField : uniqueFields.entrySet()) {
				cv = new StringMemberValue(getConstPool(clazz));
				cv.setValue(uniqueField.getKey());
				columnNameValues.add(cv);
			}

			if (CollectionUtils.isNotEmpty(columnNameValues)) {
				columnNames.setValue(columnNameValues.toArray(new MemberValue[0]));

				addAnnotations(clazz, Collections.singletonList(tableAnn));
			}
		}

	}

	protected boolean isItemType(final CtClass clazz) {
		if (!clazz.isFrozen()) {
			return getItemTypeAnnotation(clazz).isPresent();
		}

		return false;
	}

	protected Optional<Annotation> getItemTypeAnnotation(final CtClass clazz) {
		return getAnnotation(clazz, ItemType.class);
	}

	protected String getItemTypeCode(final CtClass clazz) {
		final Optional<Annotation> ann = getItemTypeAnnotation(clazz);

		if (ann.isPresent()) {
			final StringMemberValue typeCode = (StringMemberValue) ann.get().getMemberValue(MV_TYPE_CODE);

			return typeCode.getValue();
		}

		return null;
	}

	protected List<Annotation> createJpaRelationAnnotations(final CtClass entityClass, final CtField field,
			final Annotation propertyAnnotation, final Annotation relationAnnotation) throws NotFoundException {

		final List<Annotation> ann = new ArrayList<>();
		final List<Annotation> jpaAnnotations = new ArrayList<>();

		if (relationAnnotation != null) {
			final EnumMemberValue relType = (EnumMemberValue) relationAnnotation.getMemberValue(MV_TYPE);

			// JPA Relation annotations
			if (StringUtils.equals(relType.getValue(), RelationType.ManyToMany.toString())) {
				jpaAnnotations.add(createJpaRelationAnnotation(entityClass, field, ManyToMany.class));

				// JoinTable annotation for bi-directional m-to-n relation table
				jpaAnnotations
						.add(createJoinTableAnnotation(entityClass, field, propertyAnnotation, relationAnnotation));

			} else if (StringUtils.equals(relType.getValue(), RelationType.OneToMany.toString())) {
				final Annotation o2mAnn = createJpaRelationAnnotation(entityClass, field, OneToMany.class);
				addMappedByAnnotationValue(o2mAnn, entityClass, relationAnnotation);
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
		} else if (hasInterface(entityClass, Collection.class) || hasInterface(entityClass, Map.class)) {
			jpaAnnotations.add(createAnnotation(entityClass, ElementCollection.class));
		}

		if (CollectionUtils.isNotEmpty(jpaAnnotations)) {
			addAnnotations(entityClass, field, jpaAnnotations);
		}

		return ann;
	}

	protected void addMappedByAnnotationValue(final Annotation annotation, final CtClass entityClass,
			final Annotation relation) {
		if (relation != null) {
			final StringMemberValue mappedTo = (StringMemberValue) relation.getMemberValue(MV_MAPPED_TO);

			annotation.addMemberValue(MV_MAPPED_BY,
					createAnnotationStringValue(getConstPool(entityClass), mappedTo.getValue()));
		}
	}

	protected Annotation createJpaRelationAnnotation(final CtClass clazz, final CtField field,
			final Class<? extends java.lang.annotation.Annotation> annotationType) {
		final Annotation ann = createAnnotation(clazz, annotationType);
		addCascadeAnnotation(ann, field);

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
