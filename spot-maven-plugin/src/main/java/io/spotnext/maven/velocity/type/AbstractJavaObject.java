package io.spotnext.maven.velocity.type;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.spotnext.maven.velocity.Visibility;
import io.spotnext.maven.velocity.type.annotation.JavaAnnotation;

/**
 * <p>Abstract AbstractJavaObject class.</p>
 *
 * @since 1.0
 */
public abstract class AbstractJavaObject extends AbstractObject {
	private static final long serialVersionUID = 1L;

	protected String description;
	protected Visibility visibility = Visibility.PUBLIC;
	protected final Set<JavaAnnotation> annotations = new HashSet<>();

	/**
	 * <p>addAnnotation.</p>
	 *
	 * @param annotation a {@link io.spotnext.maven.velocity.type.annotation.JavaAnnotation} object.
	 */
	public void addAnnotation(JavaAnnotation annotation) {
		getImports().add(annotation.getType().getFullyQualifiedName());
		this.annotations.add(annotation);
	}

	/**
	 * <p>Getter for the field <code>description</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <p>getJavadoc.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getJavadoc() {
		return StringUtils.replace(description, "\n", "<br>");
	}

	/**
	 * <p>Setter for the field <code>description</code>.</p>
	 *
	 * @param description a {@link java.lang.String} object.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * <p>Getter for the field <code>annotations</code>.</p>
	 *
	 * @return a {@link java.util.Set} object.
	 */
	public Set<JavaAnnotation> getAnnotations() {
		return Collections.unmodifiableSet(annotations);
	}

	/**
	 * <p>Getter for the field <code>visibility</code>.</p>
	 *
	 * @return a {@link io.spotnext.maven.velocity.Visibility} object.
	 */
	public Visibility getVisibility() {
		return visibility;
	}

	/**
	 * <p>Setter for the field <code>visibility</code>.</p>
	 *
	 * @param visibility a {@link io.spotnext.maven.velocity.Visibility} object.
	 */
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}

	/** {@inheritDoc} */
	@Override
	public Set<String> getImports() {
		final Set<String> allImports = super.getImports();
		allImports.addAll(annotations.stream().flatMap(i -> i.getImports().stream()).collect(Collectors.toSet()));

		return allImports;
	}
}
