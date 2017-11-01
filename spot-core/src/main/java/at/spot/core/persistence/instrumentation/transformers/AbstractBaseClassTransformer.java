package at.spot.core.persistence.instrumentation.transformers;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.spot.core.infrastructure.annotation.ItemType;
import ch.qos.logback.core.util.CloseUtil;
import de.invesdwin.instrument.transformer.IllegalClassTransformationException;
import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;

/**
 * Transforms custom {@link ItemType} annotations to JPA entity annotations.
 */
public abstract class AbstractBaseClassTransformer implements ClassFileTransformer {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractBaseClassTransformer.class);
	protected ClassPool pool = ClassPool.getDefault();

	@Override
	public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
			final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {

		String classId = className;

		if (StringUtils.isNotBlank(classId)) {
			classId = classId.replaceAll("/", ".");

			pool.insertClassPath(new ByteArrayClassPath(classId, classfileBuffer));

			CtClass clazz = null;

			if (!classId.contains("$Proxy") && !classId.contains("sun/reflect")) {
				try {
					clazz = pool.get(classId);

				} catch (final NotFoundException e) {
					LOG.warn(String.format("Could not process class '%s'", classId));
					throw new IllegalClassTransformationException(String.format("Could not load class %s", classId), e);
				}

				if (clazz != null) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(String.format("Processing class '%s'", clazz.getName()));
					}
					final Optional<CtClass> transformedClass = transform(loader, clazz, classBeingRedefined,
							protectionDomain);

					if (transformedClass.isPresent()) {
						try {
							return transformedClass.get().toBytecode();
						} catch (IOException | CannotCompileException e) {
							throw new IllegalClassTransformationException(
									String.format("Could not compile transformed class %s", classId), e);
						}
					}
				}
			} else {
				LOG.debug(String.format("Ignoring proxy class %s", classId));
			}
		} else {
			LOG.debug("Ignoring class with empty name");
		}

		return null;
	}

	/**
	 * 
	 * 
	 * @param loader
	 *            the defining loader of the class to be transformed, may be null if
	 *            the bootstrap loader
	 * @param clazz
	 *            the class in the internal form of the JVM.
	 * @param classBeingRedefined
	 *            if this is triggered by a redefine or retransform, the class being
	 *            redefined or retransformed; if this is a class load, null
	 * @param protectionDomain
	 *            the protection domain of the class being defined or redefined
	 * @return the transformed class object. If the class was not changed, return
	 *         null instead.
	 */
	abstract protected Optional<CtClass> transform(final ClassLoader loader, final CtClass clazz,
			final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain)
			throws IllegalClassTransformationException;

	/**
	 * Returns the annotation for the given class.
	 * 
	 * @param clazz
	 * @param annotation
	 * @return
	 */
	protected List<Annotation> getAnnotations(final CtClass clazz) {
		final List<Annotation> annotations = new ArrayList<>();

		ClassFile clazzFile = clazz.getClassFile2();

		if (clazzFile == null && !clazz.isFrozen()) {
			clazzFile = clazz.getClassFile();
		}

		final AnnotationsAttribute attInfo = (AnnotationsAttribute) clazzFile
				.getAttribute(AnnotationsAttribute.visibleTag);

		if (attInfo != null) {
			annotations.addAll(Arrays.asList(attInfo.getAnnotations()));
		}

		return annotations;
	}

	/**
	 * Returns the annotation for the given class.
	 * 
	 * @param clazz
	 * @param annotation
	 * @return
	 */
	protected Optional<Annotation> getAnnotation(final CtClass clazz,
			final Class<? extends java.lang.annotation.Annotation> annotation) {
		return getAnnotations(clazz).stream().filter(a -> StringUtils.equals(a.getTypeName(), annotation.getName()))
				.findFirst();
	}

	/**
	 * Returns all annotations of the given field.
	 * 
	 * @param field
	 * @return
	 */
	protected List<Annotation> getAnnotations(final CtField field) {
		final FieldInfo info = field.getFieldInfo();

		final AnnotationsAttribute attInfo = (AnnotationsAttribute) info.getAttribute(AnnotationsAttribute.visibleTag);

		if (attInfo != null) {
			return Arrays.asList(attInfo.getAnnotations());
		}

		return Collections.emptyList();
	}

	/**
	 * Returns the given annotation.
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
	 * Returns all annotations of the given method.
	 * 
	 * @param field
	 * @return
	 */
	protected List<Annotation> getAnnotations(final CtMethod method) {
		final MethodInfo info = method.getMethodInfo();

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
	protected Optional<Annotation> getAnnotation(final CtMethod method,
			final Class<? extends java.lang.annotation.Annotation> annotation) {

		return getAnnotations(method).stream().filter(a -> StringUtils.equals(a.getTypeName(), annotation.getName()))
				.findFirst();
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

		return createAnnotation(clazz.getClassFile2().getConstPool(), type);
	}

	protected Annotation createAnnotation(final ConstPool cpool,
			final Class<? extends java.lang.annotation.Annotation> type) {

		final Annotation annotation = new Annotation(type.getName(), cpool);

		return annotation;
	}

	/**
	 * Adds the given annotation to a class field.
	 * 
	 * @param clazz
	 * @param field
	 * @param annotation
	 */
	protected void addAnnotations(final CtClass clazz, final CtField field, final List<Annotation> annotations) {
		for (final Annotation a : annotations) {
			final AnnotationsAttribute attr = (AnnotationsAttribute) field.getFieldInfo()
					.getAttribute(AnnotationsAttribute.visibleTag);
			attr.addAnnotation(a);
			field.getFieldInfo().addAttribute(attr);
		}
	}

	protected void addAnnotations(final CtClass clazz, final List<Annotation> annotations) {
		final List<Annotation> allAnnotations = getAnnotations(clazz);
		allAnnotations.addAll(annotations);

		final AnnotationsAttribute attInfo = (AnnotationsAttribute) clazz.getClassFile()
				.getAttribute(AnnotationsAttribute.visibleTag);

		attInfo.setAnnotations(allAnnotations.toArray(new Annotation[0]));
	}

	protected ConstPool getConstPool(final CtClass clazz) {
		final ClassFile cfile = clazz.getClassFile();
		final ConstPool cpool = cfile.getConstPool();

		return cpool;
	}

	/**
	 * Returns all accessible fields (even from super classes) for the given class.
	 * 
	 * @param clazz
	 * @return
	 */
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

	/**
	 * Returns all accessible methods (even from super classes) for the given class.
	 * 
	 * @param clazz
	 * @return
	 */
	protected List<CtMethod> getDeclaredMethods(final CtClass clazz) {
		final List<CtMethod> methods = new ArrayList<>();

		try {
			for (CtClass c = clazz; c != null; c = c.getSuperclass()) {
				for (final CtMethod method : c.getDeclaredMethods()) {
					methods.add(method);
				}
			}
		} catch (final NotFoundException e) {
			// ignore, end of class hierarchy
		}

		return methods;
	}

	protected void writeClass(final CtClass clazz, final File file) throws IOException {
		final DataOutputStream out = new DataOutputStream(new FileOutputStream(file));

		try {
			clazz.toBytecode(out);
		} catch (CannotCompileException | IOException e) {
			throw new IOException(String.format("Cannot write class %s to file", clazz.getName()), e);
		} finally {
			CloseUtil.closeQuietly(out);
		}
	}

	protected boolean hasInterface(final CtClass clazz, final Object interfaceType) {
		for (final String i : clazz.getClassFile().getInterfaces()) {
			if (StringUtils.equals(interfaceType.getClass().getName(), i)) {
				return true;
			}
		}

		return false;
	}

	protected ArrayMemberValue createAnnotationMemberValueArray(final ConstPool constPool,
			final MemberValue... values) {

		final ArrayMemberValue array = new ArrayMemberValue(constPool);
		// array.setType(CascadeType.class.getName());
		array.setValue(values);

		return array;
	}
}
