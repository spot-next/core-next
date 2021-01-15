package io.spotnext.test.serialzation;

import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import io.spotnext.core.infrastructure.exception.DeserializationException;
import io.spotnext.core.infrastructure.strategy.impl.DefaultXmlSerializationStrategy;
import io.spotnext.core.testing.AbstractIntegrationTest;
import io.spotnext.itemtype.core.user.User;

public class DefaultXmlSerializationStrategyIT extends AbstractIntegrationTest {

	@Autowired
	DefaultXmlSerializationStrategy xmlSerializationStrategy;

	@Override
	protected void prepareTest() {
		//
	}

	@Override
	protected void teardownTest() {
		//
	}

	@Test
	public void test_itemWithRelations() throws DeserializationException {
		final User user = modelService.get(User.class, Collections.singletonMap(User.PROPERTY_UID, "tester1"));

		String xml = xmlSerializationStrategy.serialize(user);

		User deserializedUser = xmlSerializationStrategy.deserialize(xml, User.class);

		Assertions.assertEquals(user.getId(), deserializedUser.getId());
		Assertions.assertEquals(user.getUid(), deserializedUser.getUid());
		Assertions.assertEquals(user.getShortName(), deserializedUser.getShortName());
		Assertions.assertEquals(user.getPassword(), deserializedUser.getPassword());
		Assertions.assertEquals(user.getEmailAddress(), deserializedUser.getEmailAddress());
		Assertions.assertEquals(user.getShortName(), deserializedUser.getShortName());
		
		Assertions.assertEquals(user.getGroups().size(), deserializedUser.getGroups().size());
	}

}
