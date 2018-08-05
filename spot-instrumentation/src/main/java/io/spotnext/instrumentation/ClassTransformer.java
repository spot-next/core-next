package io.spotnext.instrumentation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;

import io.spotnext.instrumentation.internal.DynamicInstrumentationAgent;

/**
 * Objects that implement the {@link ClassFileTransformer} and annotated with this annotation will be picked up by the
 * {@link DynamicInstrumentationAgent} and registered with the JVM's {@link Instrumentation} implementation.
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface ClassTransformer {
    /**
     * The order in which the class transformer should be executed (0 is the first).
     */
    int order() default 99;
}
