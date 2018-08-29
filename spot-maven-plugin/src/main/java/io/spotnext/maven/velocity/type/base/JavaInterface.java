package io.spotnext.maven.velocity.type.base;

import io.spotnext.maven.velocity.TemplateFile;
import io.spotnext.maven.velocity.type.AbstractComplexJavaType;

/**
 * <p>JavaInterface class.</p>
 *
 * @since 1.0
 */
@TemplateFile("interface.vm")
public class JavaInterface extends AbstractComplexJavaType {
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for JavaInterface.</p>
	 */
	public JavaInterface() {
	}

	/**
	 * <p>Constructor for JavaInterface.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param packagePath a {@link java.lang.String} object.
	 */
	public JavaInterface(final String name, final String packagePath) {
		super(name, packagePath);
	}

	/**
	 * <p>Constructor for JavaInterface.</p>
	 *
	 * @param clazz a {@link java.lang.Class} object.
	 */
	public JavaInterface(final Class<?> clazz) {
		super(clazz);
	}

}
