package at.spot.maven.velocity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@TemplateFile("enum.vm")
public class JavaEnum extends JavaClass {
	private static final long serialVersionUID = 1L;

	protected final List<JavaEnumValue> values = new ArrayList<>();

	public JavaEnum() {
		this.setSuperClass(Enum.class);
	}

	public List<JavaEnumValue> getValues() {
		return Collections.unmodifiableList(values);
	}

	public void addValue(JavaEnumValue value) {
		this.values.add(value);
	}

	@Override
	public void setSuperClass(JavaInterface superClass) {
		// ignore, as we always have to use Enum.class
	}
}
