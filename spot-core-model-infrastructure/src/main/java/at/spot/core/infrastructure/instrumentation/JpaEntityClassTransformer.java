package at.spot.core.infrastructure.instrumentation;

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
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CollectionType;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.maven.xml.DatabaseColumnType;
import at.spot.core.infrastructure.type.RelationCollectionType;
import at.spot.core.infrastructure.type.RelationNodeType;
import at.spot.core.infrastructure.type.RelationType;
import at.spot.core.types.Item;
import at.spot.instrumentation.ClassTransformer;
import at.spot.instrumentation.transformer.AbstractBaseClassTransformer;
import at.spot.instrumentation.transformer.IllegalClassTransformationException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * Transforms custom {@link ItemType} annotations to JPA entity annotations.
 */
@ClassTransformer
public class JpaEntityClassTransformer extends AbstractBaseClassTransformer {

	private static final Logger LOG = LoggerFactory.getLogger(JpaEntityClassTransformer.class);

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
	protected static final String MV_NULLABLE = "nullable";
	protected static final String MV_COLUMN_NAMES = "columnNames";
	protected static final String MV_UNIQUE_CONSTRAINTS = "uniqueConstraints";
	protected static final String MV_COLUMN_TYPE = "columnType";
	protected static final String RELATION_SOURCE_COLUMN = "source_pk";
	protected static final String RELATION_TARGET_COLUMN = "target_pk";

	@SuppressFBWarnings({ "RV_RETURN_VALUE_IGNORED_BAD_PRACTICE", "REC_CATCH_EXCEPTION" })
	@Override
	protected Optional<CtClass> transform(final ClassLoader loader, final CtClass clazz,
			final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain)
			throws IllegalClassTransformationException {

		try {
			// we only want to transform item types only ...
			if (!clazz.isFrozen() && isItemType(clazz) && !alreadyTransformed(clazz)) {

				// add JPA entity annotation
				addEntityAnnotation(clazz);

				// process item properties
				for (final CtField field : getDeclaredFields(clazz)) {
					if (!clazz.equals(field.getDeclaringClass())) {
						continue;
					}

					final Optional<Annotation> propertyAnn = getAnnotation(field, Property.class);

					// process item type property annotation
					if (propertyAnn.isPresent() && isValidClass(field.getType().getName())) {
						// create the necessary JPA annotations based on
						// Relation and Property annotations
						final List<Annotation> fieldAnnotations = createJpaRelationAnnotations(clazz, field,
								propertyAnn.get());

						{ // mark property as unique
							final Optional<Annotation> uniqueAnn = createUniqueConstraintAnnotation(clazz, field,
									propertyAnn.get());

							if (uniqueAnn.isPresent()) {
								fieldAnnotations.add(uniqueAnn.get());
							}
						}

						// only add column annotation if there is no relation annotation, as this is not
						// allowed
						if (CollectionUtils.isEmpty(fieldAnnotations)) {
							final List<Annotation> columnAnn = createColumnAnnotation(clazz, field, propertyAnn.get());
							fieldAnnotations.addAll(columnAnn);
						}

						// and add them to the clazz
						addAnnotations(clazz, field, fieldAnnotations);
					}
				}

//				try {
//					final File file = new File("/var/tmp/" + clazz.getName() + CLASS_FILE_SUFFIX);
//
//					if (file.exists()) {
//						file.delete();
//					}
//
//					writeClass(clazz, file);
//				} catch (final IOException e) {
//					throw new IllegalClassTransformationException(
//							String.format("Unable to write class file %s", clazz.getName()), e);
//				}

				return Optional.of(clazz);
			}
		} catch (final Exception e) {
			LOG.error(e.getMessage(), e);

			throw new IllegalClassTransformationException(
					String.format("Unable to process JPA annotations for class file %s", clazz.getName()), e);
		}

		return Optional.empty();
	}

	protected boolean alreadyTransformed(final CtClass clazz) throws IllegalClassTransformationException {
		final Optional<Annotation> entityAnnotation = getAnnotation(clazz, Entity.class);
		final Optional<Annotation> mappedSuperclassAnnotation = getAnnotation(clazz, MappedSuperclass.class);

		return entityAnnotation.isPresent() || mappedSuperclassAnnotation.isPresent();
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
		return getItemTypeAnnotation(clazz).isPresent();
	}

	protected Optional<Annotation> getItemTypeAnnotation(final CtClass clazz)
			throws IllegalClassTransformationException {

		// if the given class is a java base class it can't be unfrozen and it would
		// throw an exception so we check for valid classes
		if (isValidClass(clazz.getName())) {
			return getAnnotation(clazz, ItemType.class);
		}

		return Optional.empty();
	}

	protected String getItemTypeCode(final CtClass clazz) throws IllegalClassTransformationException {
		final Optional<Annotation> ann = getItemTypeAnnotation(clazz);

		if (ann.isPresent()) {
			final StringMemberValue typeCode = (StringMemberValue) ann.get().getMemberValue(MV_TYPE_CODE);

			return typeCode.getValue();
		}

		return null;
	}

	protected List<Annotation> createColumnAnnotation(final CtClass clazz, final CtField field,
			final Annotation propertyAnnotation) {

		List<Annotation> ret = new ArrayList<>();

		final Annotation columnAnn = createAnnotation(field.getFieldInfo2().getConstPool(), Column.class);

		final StringMemberValue columnName = new StringMemberValue(field.getFieldInfo2().getConstPool());
		columnName.setValue(field.getName());
		columnAnn.addMemberValue("name", columnName);

		if (isUniqueProperty(field, propertyAnnotation)) {
			final BooleanMemberValue nullable = new BooleanMemberValue(field.getFieldInfo2().getConstPool());
			nullable.setValue(false);
			columnAnn.addMemberValue("nullable", nullable);
		}

		ret.add(columnAnn);

		// add the type information, if available

		EnumMemberValue colTypeVal = (EnumMemberValue) propertyAnnotation.getMemberValue(MV_COLUMN_TYPE);

		if (colTypeVal != null) {
			DatabaseColumnType columnTypeEnumVal;
			try {
				columnTypeEnumVal = DatabaseColumnType.valueOf(colTypeVal.getValue());
			} catch (Exception e) {
				columnTypeEnumVal = DatabaseColumnType.DEFAULT;
			}

			if (!DatabaseColumnType.DEFAULT.equals(columnTypeEnumVal)) {
				final Annotation typeAnn = createAnnotation(field.getFieldInfo2().getConstPool(), Type.class);

				final StringMemberValue typeName = new StringMemberValue(field.getFieldInfo2().getConstPool());
				typeName.setValue(mapColumnTypeToHibernate(columnTypeEnumVal));
				typeAnn.addMemberValue("type", typeName);

				ret.add(typeAnn);
			}
		}

		return ret;
	}

	/**
	 * Checks if the field has the {@link Property#unique()} annotation value set.
	 * If yes, then a {@link NotNull} is added. The real uniqueness constraint is
	 * checked using {@link Item#uniquenessHash()}.
	 */
	protected Optional<Annotation> createUniqueConstraintAnnotation(final CtClass clazz, final CtField field,
			final Annotation propertyAnnotation) {

		Annotation ann = null;

		if (isUniqueProperty(field, propertyAnnotation)) {
			ann = createAnnotation(field.getFieldInfo2().getConstPool(), NotNull.class);
		}

		return Optional.ofNullable(ann);
	}

	/**
	 * Checks if {@link Property#unique()} = true.
	 */
	protected boolean isUniqueProperty(final CtField field, final Annotation propertyAnnotation) {
		final BooleanMemberValue unique = (BooleanMemberValue) propertyAnnotation.getMemberValue(MV_UNIQUE);
		return unique != null ? unique.getValue() : false;
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

				// necessary for serialization
				jpaAnnotations.add(createSerializationAnnotation(entityClass, field,
						"at.spot.core.infrastructure.serialization.jackson.ItemCollectionProxySerializer"));

				// necessary for FETCH JOINS
				jpaAnnotations.addAll(createOrderedListAnnotation(entityClass, field));

				// JoinTable annotation for bi-directional m-to-n relation table
				jpaAnnotations
						.add(createJoinTableAnnotation(entityClass, field, propertyAnnotation, relAnnotation.get()));

			} else if (StringUtils.equals(relType.getValue(), RelationType.OneToMany.toString())) {
				final Annotation o2mAnn = createJpaRelationAnnotation(entityClass, field, OneToMany.class);
				addMappedByAnnotationValue(field, o2mAnn, entityClass, relAnnotation.get());
				jpaAnnotations.add(o2mAnn);

				// necessary for serialization
				jpaAnnotations.add(createSerializationAnnotation(entityClass, field,
						"at.spot.core.infrastructure.serialization.jackson.ItemCollectionProxySerializer"));
				// jpaAnnotations.add(createCollectionTypeAnnotation(entityClass,
				// field));

				// necessary for FETCH JOINS
				jpaAnnotations.addAll(createOrderedListAnnotation(entityClass, field));

			} else if (StringUtils.equals(relType.getValue(), RelationType.ManyToOne.toString())) {
				jpaAnnotations.add(createJpaRelationAnnotation(entityClass, field, ManyToOne.class));
				jpaAnnotations.add(createJoinColumnAnnotation(entityClass, field));

				// necessary for serialization
				jpaAnnotations.add(createSerializationAnnotation(entityClass, field,
						"at.spot.core.infrastructure.serialization.jackson.ItemProxySerializer"));
			} else {
				// one to one in case the field type is a subtype of Item
				jpaAnnotations.add(createJpaRelationAnnotation(entityClass, field, OneToOne.class));
			}

		} else if (isItemType(field.getType())) { // one to one in case the
													// field type is a subtype
													// of Item
			jpaAnnotations.add(createJpaRelationAnnotation(entityClass, field, ManyToOne.class));
			jpaAnnotations.add(createJoinColumnAnnotation(entityClass, field));

			// necessary for serialization
			jpaAnnotations.add(createSerializationAnnotation(entityClass, field,
					"at.spot.core.infrastructure.serialization.jackson.ItemProxySerializer"));
		} else if (hasInterface(field.getType(), Collection.class) || hasInterface(field.getType(), Map.class)) {
			jpaAnnotations.add(createAnnotation(entityClass, ElementCollection.class));

			// necessary for serialization
			jpaAnnotations.add(createSerializationAnnotation(entityClass, field,
					"at.spot.core.infrastructure.serialization.jackson.ItemCollectionProxySerializer"));
		}

		return jpaAnnotations;
	}

	/**
	 * Annotates relation collections with an {@link OrderBy} annotation to make
	 * FETCH JOINS work correctly.
	 */
	protected List<Annotation> createOrderedListAnnotation(final CtClass entityClass, final CtField field)
			throws IllegalClassTransformationException {

		final List<Annotation> annotations = new ArrayList<>();

		// final Annotation orderColumnAnn = createAnnotation(entityClass,
		// OrderColumn.class);
		// annotations.add(orderColumnAnn);

		// final StringMemberValue val = new
		// StringMemberValue(field.getFieldInfo2().getConstPool());
		// val.setValue("pk ASC");
		// orderColumnAnn.addMemberValue("value", val);

		// final Annotation listIndexAnn = createAnnotation(entityClass,
		// ListIndexBase.class);
		// annotations.add(listIndexAnn);

		return annotations;
	}

	/**
	 * Necessary to prohibit infinite loops when serializing using Jackson
	 */
	protected Annotation createSerializationAnnotation(final CtClass entityClass, final CtField field,
			final String serializerClassName) throws IllegalClassTransformationException {

		final Annotation jsonSerializeAnn = createAnnotation(entityClass, JsonSerialize.class);

		final ClassMemberValue val = new ClassMemberValue(field.getFieldInfo2().getConstPool());
		val.setValue(serializerClassName);
		jsonSerializeAnn.addMemberValue("using", val);

		return jsonSerializeAnn;
	}

	protected void addMappedByAnnotationValue(final CtField field, final Annotation annotation,
			final CtClass entityClass, final Annotation relation) {
		if (relation != null) {
			final StringMemberValue mappedTo = (StringMemberValue) relation.getMemberValue(MV_MAPPED_TO);

			if (mappedTo != null && StringUtils.isNotBlank(mappedTo.getValue())) {
				annotation.addMemberValue(MV_MAPPED_BY,
						createAnnotationStringValue(field.getFieldInfo2().getConstPool(), mappedTo.getValue()));
			}
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

	@SuppressFBWarnings("DB_DUPLICATE_BRANCHES")
	protected Annotation createCollectionTypeAnnotation(final CtClass clazz, final CtField field)
			throws IllegalClassTransformationException {

		final Annotation ann = createAnnotation(clazz, CollectionType.class);

		final Optional<Annotation> relationCollectionType = getAnnotation(field, Relation.class);

		if (relationCollectionType.isPresent()) {
			final EnumMemberValue val = (EnumMemberValue) relationCollectionType.get().getMemberValue("collectionType");

			final StringMemberValue typeVal = new StringMemberValue(field.getFieldInfo2().getConstPool());

			// TODO change to list?
			if (val == null || RelationCollectionType.List.toString().equals(val.getValue())) {
				typeVal.setValue("at.spot.core.persistence.hibernate.support.usertypes.RelationshipMaintainingSetType");
			} else if (RelationCollectionType.Set.toString().equals(val.getValue())) {
				typeVal.setValue("at.spot.core.persistence.hibernate.support.usertypes.RelationshipMaintainingSetType");
			} else {
				typeVal.setValue("at.spot.core.persistence.hibernate.support.usertypes.RelationshipMaintainingSetType");
			}

			ann.addMemberValue("type", typeVal);
		}

		return ann;

	}

	/**
	 * Creates a {@link JoinColumn} annotation annotation in case the property has a
	 * unique=true modifier.
	 */
	protected Annotation createJoinColumnAnnotation(final CtClass clazz, final CtField field)
			throws IllegalClassTransformationException {

		final Annotation ann = createAnnotation(clazz, JoinColumn.class);

		final Optional<Annotation> propAnnotation = getAnnotation(field, Property.class);

		if (propAnnotation.isPresent()) {
			final BooleanMemberValue uniqueVal = (BooleanMemberValue) propAnnotation.get().getMemberValue("unique");

			if (uniqueVal != null && uniqueVal.getValue()) {
				// unique value
				// final BooleanMemberValue unique = new
				// BooleanMemberValue(field.getFieldInfo2().getConstPool());
				// unique.setValue(true);
				//
				// ann.addMemberValue(MV_UNIQUE, unique);

				// nullable value
				final BooleanMemberValue nullable = new BooleanMemberValue(field.getFieldInfo2().getConstPool());
				nullable.setValue(false);

				ann.addMemberValue(MV_NULLABLE, nullable);
			}
		}

		// column name
		final StringMemberValue columnName = new StringMemberValue(field.getFieldInfo2().getConstPool());
		columnName.setValue(field.getName());

		ann.addMemberValue(MV_NAME, columnName);

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

	private String mapColumnTypeToHibernate(DatabaseColumnType columnType) {
		switch (columnType) {
		case CHAR:
			return "char";
		case VARCHAR:
			return "characters";
		case LONGVARCHAR:
			return "text";
		case CLOB:
			return "clob";
		case BLOB:
			return "blob";
		case TINYINT:
			return "byte";
		case SMALLINT:
			return "short";
		case INTEGER:
			return "int";
		case BIGINT:
			return "long";
		case DOUBLE:
			return "double";
		case FLOAT:
			return "float";
		case NUMERIC:
			return "big_decimal";
		case BIT:
			return "boolean";
		case DATE:
			return "date";
		case TIME:
			return "calendar_time";
		case TIMESTAMP:
			return "calendar";
		case VARBINARY:
			return "binary";
		default:
			return null;
		}
	}
}
