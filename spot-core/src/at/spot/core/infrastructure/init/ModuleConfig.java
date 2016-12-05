package at.spot.core.infrastructure.init;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Properties;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ModuleConfig {
	/**
	 * Returns the {@link Properties} for current {@link ModuleInit}.
	 * 
	 * @return
	 */
	String appConfigFile() default "";

	/**
	 * Returns the spring configuration file.
	 * 
	 * @return
	 */
	String springConfigFile() default "";

	/**
	 * Returns the spring configuration class. This overrides the
	 * {@link ModuleConfig#springConfigFile()} property.
	 * 
	 * @return
	 */
	Class<?> springConfigClass() default ModuleInit.class;
}
