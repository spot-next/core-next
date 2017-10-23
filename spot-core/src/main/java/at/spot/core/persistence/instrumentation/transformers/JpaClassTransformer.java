package at.spot.core.persistence.instrumentation.transformers;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import at.spot.core.infrastructure.annotation.ItemType;
import javassist.ClassPool;
import net.bytebuddy.ByteBuddy;

/**
 * Transforms custom {@link ItemType} annotations to JPA entity annotations.
 */
public class JpaClassTransformer implements ClassFileTransformer {

	protected final ByteBuddy buddy = new ByteBuddy();
	protected ClassPool pool = new ClassPool();

	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
			ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

		// buddy.redefine(type)
		//
		// // return classfileBuffer;
		//
		// try {
		// pool.insertClassPath(new ByteArrayClassPath(className, classfileBuffer));
		// CtClass cclass = pool.get(className.replaceAll("/", "."));
		//
		// if (!cclass.isFrozen()) {
		// for (CtMethod currentMethod : cclass.getDeclaredMethods()) {
		// Annotation annotation = getAnnotation(currentMethod);
		// if (annotation != null) {
		// List<?> parameterIndexes = getParamIndexes(annotation);
		// currentMethod.insertBefore(createJavaString(currentMethod, className,
		// parameterIndexes));
		// }
		// }
		//
		// return cclass.toBytecode();
		// }
		// } catch (Exception e) {
		// e.printStacks
//		race();
		// }
		
		loader.loadClass(className)
		return null;
	}

}
