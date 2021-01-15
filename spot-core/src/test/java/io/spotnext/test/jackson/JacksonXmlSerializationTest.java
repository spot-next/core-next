package io.spotnext.test.jackson;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.strategy.impl.DefaultXmlSerializationStrategy;
import io.spotnext.itemtype.core.user.User;

public class JacksonXmlSerializationTest extends AbstractSerializationTest {

	DefaultXmlSerializationStrategy xmlStrategy = new DefaultXmlSerializationStrategy();

	@Override
	@BeforeEach
	public void setup() throws IOException, UnknownTypeException, ClassNotFoundException {
		super.setup();

		xmlStrategy.setTypeService(typeService);
		xmlStrategy.setup();
	}

	@Test
	public void testXmlSerialization() {
		User user = mockUser();

		String xml = xmlStrategy.serialize(user);
		User deserializedUser = xmlStrategy.deserialize(xml, User.class);

		assertUser(user, deserializedUser);
	}
}