package at.spot.core.persistence.instrumentation.transformers;

import java.io.File;
import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Optional;

import javax.jdo.annotations.PersistenceCapable;

import org.apache.commons.lang3.StringUtils;
import org.zoodb.api.impl.ZooPC;

import at.spot.core.infrastructure.annotation.Accessor;
import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.type.AccessorType;
import at.spot.core.infrastructure.type.RelationType;
import at.spot.core.model.Item;
import at.spot.instrumentation.ClassTransformer;
import at.spot.instrumentation.transformer.IllegalClassTransformationException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * Transforms custom {@link ItemType} annotations to JPA entity annotations.
 */
@ClassTransformer(order = 0)
public class ZooDbEntityClassTransformer extends AbstractBaseClassTransformer {

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
	protected static final String PK_PROPERTY = "getPk";

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
				for (final CtMethod method : clazz.getMethods()) {

					// ignore jdo methods
					if (method.getName().startsWith("jdo") || method.getName().startsWith("zoo")) {
						continue;
					}

					final Optional<Annotation> accessor = getAnnotation(method, Accessor.class);

					if (accessor.isPresent()) {
						activateAccessor(method, accessor);

						// set bidirectional relation mapping for setters
						if (method.getName().startsWith("set")) {
							addBiDirectionalRelationMapping(clazz, method, accessor.get());
						}
					}
				}

				overridePkAccessor(clazz);

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

	protected void addBiDirectionalRelationMapping(final CtClass clazz, final CtMethod method,
			final Annotation accessor) throws CannotCompileException, NotFoundException {

		final StringMemberValue propertyName = (StringMemberValue) accessor.getMemberValue("propertyName");

		if (propertyName != null && StringUtils.isNotBlank(propertyName.getValue())) {
			final CtField field = clazz.getField(propertyName.getValue());
			final Optional<Annotation> relAnnotation = getAnnotation(field, Relation.class);

			if (relAnnotation.isPresent()) {
				final EnumMemberValue relType = (EnumMemberValue) relAnnotation.get().getMemberValue(MV_TYPE);

				final StringMemberValue mappedTo = (StringMemberValue) relAnnotation.get().getMemberValue("mappedTo");

				if (StringUtils.equals(relType.getValue(), RelationType.ManyToMany.toString())) {
					// groups.stream().forEach(i -> i.getMembers().add(this));

					// languages.stream().forEach(new Consumer<Language>() {
					// public void accept(Language t) {
					// i.getCountry().add(this);
					// }
					// });

					String mapper = propertyName.getValue() + ".stream().forEach(new java.util.function.Consumer() {";
					mapper += "public void accept(Language i) {";
					mapper += "i.get" + StringUtils.capitalize(mappedTo.getValue()) + "().add(this);";
					mapper += "}});";

					method.insertBefore(mapper);

				} else if (StringUtils.equals(relType.getValue(), RelationType.OneToMany.toString())) {

				} else if (StringUtils.equals(relType.getValue(), RelationType.ManyToOne.toString())) {

				} else {
					// one to one in case the field type is a subtype of Item

				}
			}
		}
	}

	/**
	 * Overrides the {@link Item#getPk()} method to return
	 * {@link ZooPC#jdoZooGetOid()}.
	 * 
	 * @param clazz
	 * @throws CannotCompileException
	 * @throws IllegalClassTransformationException
	 */
	protected void overridePkAccessor(final CtClass clazz)
			throws CannotCompileException, IllegalClassTransformationException {

		final CtMethod getter = CtNewMethod.make("public Long getPk() { return Long.valueOf(this.jdoZooGetOid()); }",
				clazz);
		final Annotation ann = createAnnotation(getConstPool(clazz), Override.class);
		addAnnotations(getter, Arrays.asList(ann));
		clazz.addMethod(getter);
	}

	protected void activateAccessor(final CtMethod method, final Optional<Annotation> accessor)
			throws CannotCompileException {
		final EnumMemberValue accessorType = (EnumMemberValue) accessor.get().getMemberValue("type");

		if (accessorType != null) {
			if (AccessorType.GETTER.name().equals(accessorType.getValue())) {
				method.insertBefore("zooActivateRead();");
			} else if (AccessorType.SETTER.name().equals(accessorType.getValue())) {
				method.insertBefore("zooActivateWrite();");
			}
		}
	}

	protected void addEntityAnnotation(final CtClass clazz) throws IllegalClassTransformationException {
		final Optional<Annotation> itemTypeAnn = getItemTypeAnnotation(clazz);

		if (itemTypeAnn.isPresent()) {
			final BooleanMemberValue val = (BooleanMemberValue) itemTypeAnn.get().getMemberValue(MV_PERSISTABLE);

			if (val != null && val.getValue()) {
				addAnnotations(clazz, Arrays.asList(createAnnotation(clazz, PersistenceCapable.class)));
			} else {
				// this type is not an persistable entity
			}
		}
	}

	protected boolean isItemType(final CtClass clazz) throws IllegalClassTransformationException {
		return getItemTypeAnnotation(clazz).isPresent();
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
}
