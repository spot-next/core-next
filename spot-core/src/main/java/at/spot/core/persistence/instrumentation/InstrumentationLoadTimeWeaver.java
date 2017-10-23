package at.spot.core.persistence.instrumentation;

import java.lang.instrument.ClassFileTransformer;
import java.util.List;

/**
 * Extends spring's
 * {@link org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver}
 * so that it can be initialized with a list of transformers.
 */
public class InstrumentationLoadTimeWeaver
		extends org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver {

	public void setTransformers(List<ClassFileTransformer> transformers) {
		if (transformers != null) {
			for (ClassFileTransformer transformer : transformers) {
				super.addTransformer(transformer);
			}
		}
	}
}
