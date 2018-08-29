package io.spotnext.core.infrastructure.annotation.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Target;

/**
 * <p>DateBetween class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Target({ FIELD, METHOD })
public @interface DateBetween {

}
