package io.spotnext.test.jackson;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.strategy.impl.DefaultJsonSerializationStrategy;
import io.spotnext.itemtype.core.catalog.Catalog;
import io.spotnext.itemtype.core.user.User;

public class JacksonJsonSerializationTest extends AbstractSerializationTest {

	private DefaultJsonSerializationStrategy jsonStrategy = new DefaultJsonSerializationStrategy();

	@Override
	@BeforeEach
	public void setup() throws IOException, UnknownTypeException, ClassNotFoundException {
		super.setup();

		jsonStrategy.setTypeService(typeService);
		jsonStrategy.setup();
	}

	@Test
	public void testSerialization() {
		User user = mockUser();

		String text = jsonStrategy.serialize(user);
		User deserializedUser = jsonStrategy.deserialize(text, User.class);

		assertUser(user, deserializedUser);

		Catalog sourceCatalog = mockCatalog(1);
		Catalog destCatalog = mockCatalog(2);

		Assertions.assertEquals(1, sourceCatalog.getVersions().size());
		Assertions.assertEquals(2, destCatalog.getVersions().size());

		text = jsonStrategy.serialize(sourceCatalog);
		destCatalog = jsonStrategy.deserialize(text, destCatalog);

		Assertions.assertEquals(sourceCatalog.getVersions().size(), destCatalog.getVersions().size());
	}
}