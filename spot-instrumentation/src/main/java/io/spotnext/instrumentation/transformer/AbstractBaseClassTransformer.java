package io.spotnext.instrumentation.transformer;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import javassist.ByteArrayClassPath;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.StringMemberValue;

public abstract class AbstractBaseClassTransformer implements ClassFileTransformer {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractBaseClassTransformer.class);

	protected final List<String> classPaths = new ArrayList<>();

	@Override
	@SuppressFBWarnings(value = "PZLA_PREFER_ZERO_LENGTH_ARRAYS", justification = "needed according to the java specs")
	public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
			final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {

		final ClassPool pool = new ClassPool(true);

		if (StringUtils.isNotBlank(className)) {
			final String classId = className.replaceAll("/", ".");

			pool.insertClassPath(new ClassClassPath(this.getClass()));
			pool.insertClassPath(new ByteArrayClassPath(classId, classfileBuffer));

			// why is javassist.DirClassPath not public?
			for (final String classPath : classPaths) {
				pool.insertClassPath(new FileClassPath(classPath));
			}

			CtClass clazz = null;

			if (isValidClass(classId)) {
				try {
					clazz = pool.get(classId);

				} catch (final NotFoundException e) {
					final String message = String.format("Could not process class '%s'", classId);
					LOG.error(message, e);
					throw new IllegalClassTransformationException(message, e);
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
						} catch (final Exception e) {
							final String message = String.format("Could not compile transformed class %s", classId);
							LOG.error(message, e);
							throw new IllegalClassTransformationException(message, e);
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

	protected boolean isValidClass(final String className) {
		return !className.contains("$Proxy") && !className.contains("sun/reflect") && !className.contains("java.lang");
	}

	/**
	 * 
	 * 
	 * @param loader
	 *            the defining loader of the class to be transformed, may be
	 *            null if the bootstrap loader
	 * @param clazz
	 *            the class in the internal form of the JVM.
	 * @param classBeingRedefined
	 *            if this is triggered by a redefine or retransform, the class
	 *            being redefined or retransformed; if this is a class load,
	 *            null
	 * @param protectionDomain
	 *            the protection domain of the class being defined or redefined
	 * @return the transformed class object. If the class was not changed,
	 *         return null instead.
	 */
	abstract protected Optional<CtClass> transform(final ClassLoader loader, final CtClass clazz,
			final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain)
			throws IllegalClassTransformationException;

	/**
	 * Returns the annotation for the given class.
	 * 
	 * @param clazz
	 * @param annotation
	 * @throws IllegalClassTransformationException
	 */
	protected List<Annotation> getAnnotations(final CtClass clazz) throws IllegalClassTransformationException {
		final List<Annotation> annotations = new ArrayList<>();

		final ClassFile clazzFile = getClassFile(clazz, true);

		final AttributeInfo attInfo = clazzFile.getAttribute(AnnotationsAttribute.visibleTag);

		if (attInfo != null && attInfo instanceof AnnotationsAttribute) {
			annotations.addAll(Arrays.asList(((AnnotationsAttribute) attInfo).getAnnotations()));
		}

		return annotations;
	}

	/**
	 * Returns the {@link ClassFile} of the given class. If defrost = true, and
	 * the {@link CtClass#getClassFile2()} is null, the class is defrosted and
	 * {@link CtClass#getClassFile()} is returned instead.
	 * 
	 * @throws IllegalClassTransformationException
	 */
	protected ClassFile getClassFile(final CtClass clazz, final boolean defrost)
			throws IllegalClassTransformationException {
		ClassFile clazzFile = null;

		if (clazz.isFrozen() && defrost) {
			clazz.defrost();
			clazzFile = clazz.getClassFile2();
		} else {
			clazzFile = clazz.getClassFile();
		}

		if (clazzFile == null) {
			throw new IllegalClassTransformationException(
					String.format("Could not get ConstPool of class %s", clazz.getName()));
		}

		return clazzFile;
	}

	/**
	 * Returns the annotation for the given class.
	 * 
	 * @param clazz
	 * @param annotation
	 * @throws IllegalClassTransformationException
	 */
	protected Optional<Annotation> getAnnotation(final CtClass clazz,
			final Class<? extends java.lang.annotation.Annotation> annotation)
			throws IllegalClassTransformationException {

		return getAnnotations(clazz).stream().filter(a -> StringUtils.equals(a.getTypeName(), annotation.getName()))
				.findFirst();
	}

	/**
	 * Returns all annotations of the given field.
	 * 
	 * @param field
	 */
	protected List<Annotation> getAnnotations(final CtField field) {
		final FieldInfo info = field.getFieldInfo2();

		final AttributeInfo attInfo = info.getAttribute(AnnotationsAttribute.visibleTag);

		if (attInfo != null && attInfo instanceof AnnotationsAttribute) {
			return Arrays.asList(((AnnotationsAttribute) attInfo).getAnnotations());
		}

		return Collections.emptyList();
	}

	/**
	 * Returns the given annotation.
	 * 
	 * @param field
	 * @param annotation
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
	 */
	protected List<Annotation> getAnnotations(final CtMethod method) {
		final MethodInfo info = method.getMethodInfo2();

		final AttributeInfo attInfo = info.getAttribute(AnnotationsAttribute.visibleTag);

		if (attInfo != null && attInfo instanceof AnnotationsAttribute) {
			return Arrays.asList(((AnnotationsAttribute) attInfo).getAnnotations());
		}

		return Collections.emptyList();
	}

	/**
	 * Returns the the given annotation.
	 * 
	 * @param field
	 * @param annotation
	 */
	protected Optional<Annotation> getAnnotation(final CtMethod method,
			final Class<? extends java.lang.annotation.Annotation> annotation) {

		return getAnnotations(method).stream().filter(a -> StringUtils.equals(a.getTypeName(), annotation.getName()))
				.findFirst();
	}

	/**
	 * Creates a new javassist annotation for the given class.
	 * 
	 * @param clazz
	 * @param type
	 * @throws IllegalClassTransformationException
	 */
	protected Annotation createAnnotation(final CtClass clazz,
			final Class<? extends java.lang.annotation.Annotation> type) throws IllegalClassTransformationException {

		return createAnnotation(getClassFile(clazz, true).getConstPool(), type);
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
			final AttributeInfo info = field.getFieldInfo2().getAttribute(AnnotationsAttribute.visibleTag);

			if (info != null && info instanceof AnnotationsAttribute) {
				final AnnotationsAttribute attr = (AnnotationsAttribute) info;
				attr.addAnnotation(a);
				field.getFieldInfo2().addAttribute(attr);
			}
		}
	}

	protected void addAnnotations(final CtClass clazz, final List<Annotation> annotations)
			throws IllegalClassTransformationException {
		final List<Annotation> allAnnotations = getAnnotations(clazz);
		allAnnotations.addAll(annotations);

		final AttributeInfo info = clazz.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);

		if (info != null && info instanceof AnnotationsAttribute) {
			final AnnotationsAttribute attInfo = (AnnotationsAttribute) info;
			attInfo.setAnnotations(allAnnotations.toArray(new Annotation[0]));
		}
	}

	protected ConstPool getConstPool(final CtClass clazz) throws IllegalClassTransformationException {
		final ClassFile cfile = getClassFile(clazz, false);

		return cfile.getConstPool();
	}

	/**
	 * Returns all accessible fields (even from super classes) for the given
	 * class.
	 * 
	 * @param clazz
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
	 * Returns all accessible methods (even from super classes) for the given
	 * class.
	 * 
	 * @param clazz
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
			try {
				out.close();
			} catch (final Exception e) {
				// silently ignore
			}
		}
	}

	protected boolean hasInterface(final CtClass clazz, final Class<?> interfaceType) {

		if (!isValidClass(clazz.getName())) {
			return false;
		}

		for (final CtClass i : getInterfaces(clazz)) {
			if (StringUtils.equals(interfaceType.getName(), i.getName())) {
				return true;
			}
		}

		return false;
	}

	protected List<CtClass> getInterfaces(final CtClass clazz) {
		List<CtClass> interfaces;

		try {
			interfaces = Arrays.asList(clazz.getInterfaces());
		} catch (final NotFoundException e) {
			// ignore
			interfaces = Collections.emptyList();
		}

		return interfaces;
	}

	protected ArrayMemberValue createAnnotationArrayValue(final ConstPool constPool, final MemberValue... values) {

		final ArrayMemberValue array = new ArrayMemberValue(constPool);
		array.setValue(values);

		return array;
	}

	protected StringMemberValue createAnnotationStringValue(final ConstPool constPool, final String value) {
		final StringMemberValue val = new StringMemberValue(constPool);
		val.setValue(value);

		return val;
	}

	public void addClassPaths(final String... classPaths) {
		this.classPaths.addAll(Arrays.asList(classPaths));
	}

	public void addClassPaths(final List<String> classPaths) {
		this.classPaths.addAll(classPaths);
	}

	public List<String> getClassPaths() {
		return classPaths;
	}

	public static class FileClassPath implements ClassPath {
		private final String classPath;

		public FileClassPath(final String classPath) {
			this.classPath = classPath;
		}

		@Override
		public InputStream openClassfile(final String classname) {
			try {
				final char sep = File.separatorChar;
				final String filename = classPath + sep + classname.replace('.', sep) + ".class";
				return new FileInputStream(filename);
			} catch (final FileNotFoundException | SecurityException e) {
				//
			}
			return null;
		}

		@Override
		public URL find(final String classname) {
			final char sep = File.separatorChar;
			final String filename = classPath + sep + classname.replace('.', sep) + ".class";
			final File f = new File(filename);
			if (f.exists()) {
				try {
					return f.getCanonicalFile().toURI().toURL();
				} catch (final IOException e) {
					//
				}
			}

			return null;
		}
	}
}
