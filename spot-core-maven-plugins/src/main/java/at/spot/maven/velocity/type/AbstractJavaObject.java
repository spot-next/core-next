package at.spot.maven.velocity.type;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import at.spot.maven.velocity.Visibility;
import at.spot.maven.velocity.type.annotation.JavaAnnotation;

public abstract class AbstractJavaObject extends AbstractObject {
	private static final long serialVersionUID = 1L;

	protected String description;
	protected Visibility visiblity = Visibility.PUBLIC;
	protected final Set<JavaAnnotation> annotations = new HashSet<>();

	public void addAnnotation(JavaAnnotation annotation) {
		getImports().add(annotation.getType().getFullyQualifiedName());
		this.annotations.add(annotation);
	}

	public String getDescription() {
		return description;
	}

	public String getJavadoc() {
		return StringUtils.replace(description, "\n", "<br>");
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<JavaAnnotation> getAnnotations() {
		return Collections.unmodifiableSet(annotations);
	}

	public Visibility getVisiblity() {
		return visiblity;
	}

	public void setVisiblity(Visibility visiblity) {
		this.visiblity = visiblity;
	}

	@Override
	public Set<String> getImports() {
		final Set<String> allImports = super.getImports();
		allImports.addAll(annotations.stream().flatMap(i -> i.getImports().stream()).collect(Collectors.toSet()));

		return allImports;
	}
}
