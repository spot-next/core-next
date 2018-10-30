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
import java.util.function.Consumer;

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
import javassist.LoaderClassPath;
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

/**
 * <p>
 * Abstract AbstractBaseClassTransformer class.
 * </p>
 *
 * @since 1.0
 */
public abstract class AbstractBaseClassTransformer implements ClassFileTransformer {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractBaseClassTransformer.class);

	protected final List<String> classPaths = new ArrayList<>();
	protected Consumer<Throwable> errorLogger = null;

	/** {@inheritDoc} */
	@Override
	@SuppressFBWarnings(value = "PZLA_PREFER_ZERO_LENGTH_ARRAYS", justification = "needed according to the java specs")
	public byte[] transform(final ClassLoader loader, final String className, final Class<?> classBeingRedefined,
			final ProtectionDomain protectionDomain, final byte[] classfileBuffer) throws IllegalClassFormatException {

		final ClassPool classPool = new ClassPool(ClassPool.getDefault());
		classPool.childFirstLookup = true;
		classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
		classPool.appendSystemPath();

		if (StringUtils.isNotBlank(className)) {
			// the className is using the OS-specific path separator
			String classId = className.replace(File.separator, ".");
			// when replacing the path separator on windows, the string is prefixed with a dot? so remove it
			classId = StringUtils.removeStart(classId, ".");

			classPool.insertClassPath(new ByteArrayClassPath(classId, classfileBuffer));

			// why is javassist.DirClassPath not public?
			for (final String classPath : classPaths) {
				classPool.insertClassPath(new FileClassPath(classPath));
			}

			classPool.insertClassPath(new ClassClassPath(this.getClass()));

			CtClass clazz = null;

			if (isValidClass(classId)) {
				try {
					clazz = classPool.get(classId);

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
			throw new IllegalClassTransformationException("Invalid empty class name");
		}

		return null;
	}

	/**
	 * Filters primitive types, proxy, internal and java base types.
	 *
	 * @param className the class name to check
	 * @return true if not filtered out by the above definition.
	 */
	protected boolean isValidClass(final String className) {
		return className.contains(".") && !className.contains("$Proxy") && !className.contains("sun/reflect")
				&& !className.contains("java.lang");
	}

	/**
	 * <p>
	 * transform.
	 * </p>
	 *
	 * @param loader              the defining loader of the class to be transformed, may be null if the bootstrap loader
	 * @param clazz               the class in the internal form of the JVM.
	 * @param classBeingRedefined if this is triggered by a redefine or retransform, the class being redefined or retransformed; if this is a class load, null
	 * @param protectionDomain    the protection domain of the class being defined or redefined
	 * @return the transformed class object. If the class was not changed, return null instead.
	 * @throws IllegalClassTransformationException in case there is an error
	 */
	abstract protected Optional<CtClass> transform(final ClassLoader loader, final CtClass clazz,
			final Class<?> classBeingRedefined, final ProtectionDomain protectionDomain)
			throws IllegalClassTransformationException;

	/**
	 * Returns the annotation for the given class.
	 *
	 * @param clazz a {@link javassist.CtClass} object.
	 * @return a {@link java.util.List} object.
	 * @throws IllegalClassTransformationException in case there is an error
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
	 * Returns the {@link javassist.bytecode.ClassFile} of the given class. If defrost = true, and the {@link javassist.CtClass#getClassFile2()} is null, the
	 * class is defrosted and {@link javassist.CtClass#getClassFile()} is returned instead.
	 *
	 * @param clazz   a {@link javassist.CtClass} object.
	 * @param defrost a boolean.
	 * @return a {@link javassist.bytecode.ClassFile} object.
	 * @throws IllegalClassTransformationException in case there is an error
	 */
	protected ClassFile getClassFile(final CtClass clazz, final boolean defrost)
			throws IllegalClassTransformationException {
		ClassFile clazzFile = null;

		if (isValidClass(clazz.getName()) && clazz.isFrozen() && defrost) {
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
	 * @param clazz      a {@link javassist.CtClass} object.
	 * @param annotation a {@link java.lang.Class} object.
	 * @return a {@link java.util.Optional} object.
	 * @throws IllegalClassTransformationException in case there is an error
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
	 * @param field a {@link javassist.CtField} object.
	 * @return a {@link java.util.List} object.
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
	 * @param field      a {@link javassist.CtField} object.
	 * @param annotation a {@link java.lang.Class} object.
	 * @return a {@link java.util.Optional} object.
	 */
	protected Optional<Annotation> getAnnotation(final CtField field,
			final Class<? extends java.lang.annotation.Annotation> annotation) {

		return getAnnotations(field).stream().filter(a -> StringUtils.equals(a.getTypeName(), annotation.getName()))
				.findFirst();
	}

	/**
	 * Returns all annotations of the given method.
	 *
	 * @param method a {@link javassist.CtMethod} object.
	 * @return a {@link java.util.List} object.
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
	 * @param annotation a {@link java.lang.Class} object.
	 * @param method     a {@link javassist.CtMethod} object.
	 * @return a {@link java.util.Optional} object.
	 */
	protected Optional<Annotation> getAnnotation(final CtMethod method,
			final Class<? extends java.lang.annotation.Annotation> annotation) {

		return getAnnotations(method).stream().filter(a -> StringUtils.equals(a.getTypeName(), annotation.getName()))
				.findFirst();
	}

	/**
	 * Creates a new javassist annotation for the given class.
	 *
	 * @param clazz a {@link javassist.CtClass} object.
	 * @param type  a {@link java.lang.Class} object.
	 * @return a {@link javassist.bytecode.annotation.Annotation} object.
	 * @throws IllegalClassTransformationException in case there is an error
	 */
	protected Annotation createAnnotation(final CtClass clazz,
			final Class<? extends java.lang.annotation.Annotation> type) throws IllegalClassTransformationException {

		return createAnnotation(getClassFile(clazz, true).getConstPool(), type);
	}

	/**
	 * Creates a new {@link java.lang.annotation.Annotation} instance using the given annotation type.
	 * 
	 * @param cpool          the constpool to use
	 * @param annotationType
	 * @return
	 */
	protected Annotation createAnnotation(final ConstPool cpool,
			final Class<? extends java.lang.annotation.Annotation> type) {

		return createAnnotation(cpool, type.getName());
	}

	/**
	 * Creates a new {@link java.lang.annotation.Annotation} instance using the given fully qualified annotation name.
	 * 
	 * @param cpool          the constpool to use
	 * @param annotationType
	 * @return
	 */
	protected Annotation createAnnotation(final ConstPool cpool,
			final String annotationType) {

		final Annotation annotation = new Annotation(annotationType, cpool);

		return annotation;
	}

	/**
	 * Adds the given annotation to a class field.
	 *
	 * @param clazz       a {@link javassist.CtClass} object.
	 * @param field       a {@link javassist.CtField} object.
	 * @param annotations a {@link java.util.List} object.
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

	/**
	 * Adds the given annotations to the given class.
	 *
	 * @param clazz       a {@link javassist.CtClass} object.
	 * @param annotations a {@link java.util.List} object.
	 * @throws io.spotnext.instrumentation.transformer.IllegalClassTransformationException if any.
	 */
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

	/**
	 * <p>
	 * getConstPool.
	 * </p>
	 *
	 * @param clazz a {@link javassist.CtClass} object.
	 * @return a {@link javassist.bytecode.ConstPool} object.
	 * @throws io.spotnext.instrumentation.transformer.IllegalClassTransformationException if any.
	 */
	protected ConstPool getConstPool(final CtClass clazz) throws IllegalClassTransformationException {
		final ClassFile cfile = getClassFile(clazz, false);

		return cfile.getConstPool();
	}

	/**
	 * Returns all accessible fields (even from super classes) for the given class.
	 *
	 * @param clazz a {@link javassist.CtClass} object.
	 * @return a {@link java.util.List} object.
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
	 * @param clazz a {@link javassist.CtClass} object.
	 * @return a {@link java.util.List} object.
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

	/**
	 * <p>
	 * writeClass.
	 * </p>
	 *
	 * @param clazz a {@link javassist.CtClass} object.
	 * @param file  a {@link java.io.File} object.
	 * @throws java.io.IOException if any.
	 */
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

	/**
	 * <p>
	 * hasInterface.
	 * </p>
	 *
	 * @param clazz         a {@link javassist.CtClass} object.
	 * @param interfaceType a {@link java.lang.Class} object.
	 * @return a boolean.
	 */
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

	/**
	 * <p>
	 * getInterfaces.
	 * </p>
	 *
	 * @param clazz a {@link javassist.CtClass} object.
	 * @return a {@link java.util.List} object.
	 */
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

	/**
	 * <p>
	 * createAnnotationArrayValue.
	 * </p>
	 *
	 * @param constPool a {@link javassist.bytecode.ConstPool} object.
	 * @param values    a {@link javassist.bytecode.annotation.MemberValue} object.
	 * @return a {@link javassist.bytecode.annotation.ArrayMemberValue} object.
	 */
	protected ArrayMemberValue createAnnotationArrayValue(final ConstPool constPool, final MemberValue... values) {

		final ArrayMemberValue array = new ArrayMemberValue(constPool);
		array.setValue(values);

		return array;
	}

	/**
	 * <p>
	 * createAnnotationStringValue.
	 * </p>
	 *
	 * @param constPool a {@link javassist.bytecode.ConstPool} object.
	 * @param value     a {@link java.lang.String} object.
	 * @return a {@link javassist.bytecode.annotation.StringMemberValue} object.
	 */
	protected StringMemberValue createAnnotationStringValue(final ConstPool constPool, final String value) {
		final StringMemberValue val = new StringMemberValue(constPool);
		val.setValue(value);

		return val;
	}

	/**
	 * <p>
	 * addClassPaths.
	 * </p>
	 *
	 * @param classPaths a {@link java.lang.String} object.
	 */
	public void addClassPaths(final String... classPaths) {
		this.classPaths.addAll(Arrays.asList(classPaths));
	}

	/**
	 * <p>
	 * addClassPaths.
	 * </p>
	 *
	 * @param classPaths a {@link java.util.List} object.
	 */
	public void addClassPaths(final List<String> classPaths) {
		this.classPaths.addAll(classPaths);
	}

	/**
	 * <p>
	 * Getter for the field <code>classPaths</code>.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 */
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

	/**
	 * @param the consumer that can do additional logging when exceptions occur. Can be null.
	 */
	public void setErrorLogger(Consumer<Throwable> errorLogger) {
		this.errorLogger = errorLogger;
	}

	protected void logException(Throwable cause) {
		LOG.error(cause.getMessage(), cause);

		if (errorLogger != null) {
			errorLogger.accept(cause);
		}
	}
}
