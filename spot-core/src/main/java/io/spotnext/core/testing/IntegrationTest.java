package io.spotnext.core.testing;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.test.context.BootstrapWith;

import io.spotnext.core.CoreInit;
import io.spotnext.core.infrastructure.support.init.ModuleInit;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@BootstrapWith(SpringBootTestContextBootstrapper.class)
public @interface IntegrationTest {
	/**
	 * The init class used to startup the spOt instance.
	 */
	Class<? extends ModuleInit> initClass() default CoreInit.class;
}
