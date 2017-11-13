package at.spot.core.persistence.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.util.List;

import org.springframework.context.weaving.DefaultContextLoadTimeWeaver;

/**
 * Extends spring's
 * {@link org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver}
 * so that it can be initialized with a list of transformers.
 */
public class InstrumentationLoadTimeWeaver extends DefaultContextLoadTimeWeaver {

	public InstrumentationLoadTimeWeaver() {
		super(InstrumentationLoadTimeWeaver.class.getClassLoader());
	}

	public void setTransformers(final List<ClassFileTransformer> transformers) {
		if (transformers != null) {
			for (final ClassFileTransformer transformer : transformers) {
				super.addTransformer(transformer);
			}
		}
	}
}
