package io.spotnext.infrastructure.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import org.hibernate.bytecode.enhance.spi.DefaultEnhancementContext;
import org.hibernate.bytecode.enhance.spi.EnhancementContext;
import org.hibernate.bytecode.enhance.spi.Enhancer;
import org.hibernate.bytecode.enhance.spi.UnloadedClass;
import org.hibernate.bytecode.enhance.spi.UnloadedField;
import org.hibernate.cfg.Environment;

/**
 * Enhances JPA entities by Hibernate's enhancer.
 */
public class HibernateEnhancer implements ClassFileTransformer {
	
	@Override
	public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer)
			throws IllegalClassFormatException {

		ClassLoader classLoader = getClass().getClassLoader();

		EnhancementContext enhancementContext = new DefaultEnhancementContext() {
			@Override
			public ClassLoader getLoadingClassLoader() {
				return classLoader;
			}

			@Override
			public boolean doBiDirectionalAssociationManagement(UnloadedField field) {
				return true;
			}

			@Override
			public boolean doDirtyCheckingInline(UnloadedClass classDescriptor) {
				return true;
			}

			@Override
			public boolean hasLazyLoadableAttributes(UnloadedClass classDescriptor) {
				return true;
			}

			@Override
			public boolean isLazyLoadable(UnloadedField field) {
				return true;
			}

			@Override
			public boolean doExtendedEnhancement(UnloadedClass classDescriptor) {
				return true;
			}
		};

		final Enhancer enhancer = Environment.getBytecodeProvider().getEnhancer(enhancementContext);
		return enhancer.enhance(className, classfileBuffer);
	}

}
