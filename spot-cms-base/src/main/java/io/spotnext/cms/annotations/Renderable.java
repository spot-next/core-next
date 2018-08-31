package io.spotnext.cms.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * When added to an item property this indicates to the CMS rendering
 * infrastructure that this property should be used exposed to an underlying
 * rendering engine.
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Renderable {

}
