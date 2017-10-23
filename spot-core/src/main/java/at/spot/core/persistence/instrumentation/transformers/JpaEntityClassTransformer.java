package at.spot.core.persistence.instrumentation.transformers;

import java.io.File;
import java.io.IOException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
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
import de.invesdwin.instrument.transformer.IllegalClassTransformationException;
import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.EnumMemberValue;

/**
 * Transforms custom {@link ItemType} annotations to JPA entity annotations.
 */
@ClassTransformer
public class JpaEntityClassTransformer extends AbstractBaseClassTransformer {

	@Override
	protected Optional<CtClass> transform(final ClassLoader loader, final CtClass clazz,
			final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain)
			throws IllegalClassTransformationException {

		// we only want to transform item types ...
		if (!clazz.isFrozen() && isItemType(clazz)) {

			// ... but only for item properties
			for (final CtField field : getDeclaredFields(clazz)) {
				final Optional<Annotation> propertyAnn = getAnnotation(field, Property.class);

				// process item type property annotation
				if (propertyAnn.isPresent()) {
					final Optional<Annotation> relationAnn = getAnnotation(field, Relation.class);

					// create the necessary JPA annotations based on Relation and Property
					// annotations
					final List<Annotation> jpaAnnotations = createJpaRelationAnnotations(clazz, field,
							propertyAnn.get(), relationAnn.orElse(null));

					// and add them to the clazz
					addAnnotations(clazz, field, jpaAnnotations);
				}
			}

			try {
				writeClass(clazz, new File("/var/tmp/" + clazz.getName() + ".class"));
			} catch (IOException e) {
				throw new IllegalClassTransformationException(
						String.format("Unable to write class file %s", clazz.getName()), e);
			}

			return Optional.of(clazz);
		}

		return Optional.empty();
	}

	protected boolean isItemType(final CtClass clazz) {
		return getAnnotation(clazz, ItemType.class).isPresent();
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

}
