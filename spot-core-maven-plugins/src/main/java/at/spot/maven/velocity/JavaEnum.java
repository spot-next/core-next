package at.spot.maven.velocity;

import java.util.ArrayList;
import java.util.List;

public class JavaEnum extends JavaClass {
	private static final long serialVersionUID = 1L;

	protected final List<JavaEnumValue> values = new ArrayList<>();

	public List<JavaEnumValue> getValues() {
		return values;
	}

}
