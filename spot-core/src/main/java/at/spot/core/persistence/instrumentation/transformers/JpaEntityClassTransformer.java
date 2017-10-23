package at.spot.core.persistence.instrumentation.transformers;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.apache.commons.lang3.StringUtils;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Property;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.type.RelationType;
import de.invesdwin.instrument.ClassTransformer;
import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.EnumMemberValue;

/**
 * Transforms custom {@link ItemType} annotations to JPA entity annotations.
 */
@ClassTransformer
public class JpaEntityClassTransformer implements ClassFileTransformer {

	protected ClassPool pool = ClassPool.getDefault();;

	@Override
	public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
			final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {

		try {
			pool.insertClassPath(new ByteArrayClassPath(className, classfileBuffer));
			final CtClass clazz = pool.get(className.replaceAll("/", "."));

			if (!clazz.isFrozen() && isItemType(clazz)) {
				for (final CtField field : getDeclaredFields(clazz)) {
					// final List<Annotation> allAnnotations =
					// getAnnotations(field);
					final Optional<Annotation> propertyAnn = getAnnotation(field, Property.class);

					// process item type property annotation
					if (propertyAnn.isPresent()) {
						final Optional<Annotation> relationAnn = getAnnotation(field, Relation.class);

						final List<Annotation> jpaAnnotations = createJpaRelationAnnotations(clazz, field,
								propertyAnn.get(), relationAnn.orElse(null));

						addAnnotations(clazz, field, jpaAnnotations);
					}

				}
				return clazz.toBytecode();
			}
		} catch (final Exception e) {
			throw new RuntimeException(String.format("Could not transform item type '%s' to JPA entity ", className),
					e);
		}

		return null;
	}

	protected boolean isItemType(final CtClass clazz) throws ClassNotFoundException {
		return clazz.getAnnotation(ItemType.class) != null;
	}

	/**
	 * Returns all annotations of the given field.
	 * 
	 * @param field
	 * @return
	 */
	protected List<Annotation> getAnnotations(final CtField field) {
		final FieldInfo info = field.getFieldInfo();

		// the attribute we are looking for is a runtime invisible attribute
		// use Retention(RetentionPolicy.RUNTIME) on the annotation to make it
		// visible at runtime
		final AnnotationsAttribute attInfo = (AnnotationsAttribute) info.getAttribute(AnnotationsAttribute.visibleTag);

		if (attInfo != null) {
			return Arrays.asList(attInfo.getAnnotations());
		}

		return Collections.emptyList();
	}

	/**
	 * Returns the the given annotation.
	 * 
	 * @param field
	 * @param annotation
	 * @return
	 */
	protected Optional<Annotation> getAnnotation(final CtField field,
			final Class<? extends java.lang.annotation.Annotation> annotation) {

		return getAnnotations(field).stream().filter(a -> StringUtils.equals(a.getTypeName(), annotation.getName()))
				.findFirst();
	}

	/**
	 * Adds the given annotation to a class field.
	 * 
	 * @param clazz
	 * @param field
	 * @param annotation
	 */
	protected void addAnnotations(final CtClass clazz, final CtField field, final List<Annotation> annotations) {

		final AnnotationsAttribute attr = new AnnotationsAttribute(getConstPool(clazz),
				AnnotationsAttribute.visibleTag);

		for (final Annotation a : annotations) {
			attr.addAnnotation(a);
		}

		field.getFieldInfo().addAttribute(attr);
	}

	protected List<Annotation> createJpaRelationAnnotations(final CtClass entityClass, final CtField field,
			final Annotation propertyAnnotation, final Annotation relationAnnotation) {

		final List<Annotation> ann = new ArrayList<>();

		if (relationAnnotation != null) {
			Annotation jpaRelation = null;

			final EnumMemberValue relType = (EnumMemberValue) relationAnnotation.getMemberValue("type");

			if (StringUtils.equals(relType.getValue(), RelationType.ManyToMany.toString())) {
				jpaRelation = createAnnotation(entityClass, ManyToMany.class);
				addCascadeAnnotation(jpaRelation, field);

			} else if (StringUtils.equals(relType.getValue(), RelationType.OneToMany.toString())) {
				jpaRelation = createAnnotation(entityClass, OneToMany.class);
				addCascadeAnnotation(jpaRelation, field);

			} else {
				// one to one in case the field type is a subtype of Item

				jpaRelation = createAnnotation(entityClass, OneToOne.class);
				addCascadeAnnotation(jpaRelation, field);
			}

		}

		return ann;
	}

	protected void addCascadeAnnotation(final Annotation annotation, final CtField field) {

		final EnumMemberValue val = new EnumMemberValue(field.getFieldInfo2().getConstPool());
		val.setType(CascadeType.class.getName());
		val.setValue(CascadeType.ALL.toString());

		annotation.addMemberValue("cascade", val);
	}

	// protected ArrayMemberValue createArrayMemberValue( final
	// List<MemberValue> values) {
	// final ArrayMemberValue val = new ArrayMemberValue(getConstPool());
	//
	// val.getType()
	// val.setValue(elements);
	//
	// return val;
	// }

	/**
	 * Creates a new javassist annotation for the given class.
	 * 
	 * @param clazz
	 * @param type
	 * @return
	 */
	protected Annotation createAnnotation(final CtClass clazz,
			final Class<? extends java.lang.annotation.Annotation> type) {

		final ConstPool cpool = getConstPool(clazz);
		final Annotation annotation = new Annotation(type.getName(), cpool);

		return annotation;
	}

	protected ConstPool getConstPool(final CtClass clazz) {
		final ClassFile cfile = clazz.getClassFile();
		final ConstPool cpool = cfile.getConstPool();

		return cpool;
	}

	protected List<CtField> getDeclaredFields(final CtClass clazz) {
		final List<CtField> fields = new ArrayList<>();

		try {
			for (CtClass c = clazz; c != null; c = c.getSuperclass()) {
				for (final CtField field : c.getDeclaredFields()) {
					fields.add(field);
				}
			}
		} catch (final NotFoundException e) {
			// ignore, end of class hierarchy
		}

		return fields;
	}
}
