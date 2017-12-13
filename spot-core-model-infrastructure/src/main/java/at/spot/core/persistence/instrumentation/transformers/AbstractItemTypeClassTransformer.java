package at.spot.core.persistence.instrumentation.transformers;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.type.RelationType;
import at.spot.instrumentation.transformer.IllegalClassTransformationException;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

/**
 * This class provides some useful functionality for item type class
 * transformers.
 */
public abstract class AbstractItemTypeClassTransformer extends AbstractBaseClassTransformer {

	protected static final String MV_TYPE = "type";
	protected static final String MV_TYPE_CODE = "typeCode";

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
