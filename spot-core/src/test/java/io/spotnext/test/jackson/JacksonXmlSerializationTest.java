package io.spotnext.test.jackson;

import java.io.IOException;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.strategy.impl.DefaultJsonSerializationStrategy;
import io.spotnext.core.infrastructure.strategy.impl.DefaultXmlSerializationStrategy;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

public class JacksonXmlSerializationTest {

	DefaultXmlSerializationStrategy xmlStrategy = new DefaultXmlSerializationStrategy();
	DefaultJsonSerializationStrategy jsonStrategy = new DefaultJsonSerializationStrategy();

	@Mock
	TypeService typeService;

	@Mock
	ApplicationContext context;

	@Before
	public void setup() throws IOException, UnknownTypeException, ClassNotFoundException {
		MockitoAnnotations.initMocks(this);

		Mockito.when(context.getBean("typeService")).thenReturn(typeService);
		Mockito.when(context.getBean(TypeService.class)).thenReturn(typeService);
		Registry.instance().setApplicationContext(context);

		Mockito.when(typeService.getClassForTypeCode(User.TYPECODE)).thenReturn((Class) User.class);
		Mockito.when(typeService.getClassForTypeCode(UserGroup.TYPECODE)).thenReturn((Class) UserGroup.class);
		Mockito.when(typeService.getTypeCodeForClass(User.class)).thenReturn(User.TYPECODE);
		Mockito.when(typeService.getTypeCodeForClass(UserGroup.class)).thenReturn(UserGroup.TYPECODE);

		xmlStrategy.setTypeService(typeService);
		xmlStrategy.setup();

		jsonStrategy.setTypeService(typeService);
		jsonStrategy.setup();
	}

	User mockUser() {
		User user = new User();
		user.setUid("userID");
		user.setShortName("user");
		user.setGroups(new HashSet<>());

		UserGroup group1 = new UserGroup();
		group1.setUid("group1ID");
		group1.setShortName("group1");
		group1.setMembers(new HashSet<>());
		group1.getMembers().add(user);

		UserGroup group2 = new UserGroup();
		group2.setUid("group2ID");
		group2.setShortName("group2");
		group2.setMembers(new HashSet<>());
		group2.getMembers().add(user);

		user.groups.add(group1);
		user.groups.add(group2);

		return user;
	}

	@Test
	public void testXmlSerialization() {
		User user = mockUser();

		String xml = xmlStrategy.serialize(user);
		User deserializedUser = xmlStrategy.deserialize(xml, User.class);

		assertUser(user, deserializedUser);
	}

	@Test
	public void testJsonSerialization() {
		User user = mockUser();

		String xml = jsonStrategy.serialize(user);
		User deserializedUser = jsonStrategy.deserialize(xml, User.class);

		assertUser(user, deserializedUser);
	}

	void assertUser(User expected, User deserializedUser) {
		Assert.assertEquals(expected.getGroups().size(), deserializedUser.getGroups().size());
		Assert.assertEquals(expected.getPk(), deserializedUser.getPk());
		Assert.assertEquals(expected.getUid(), deserializedUser.getUid());
		Assert.assertEquals(expected.getEmailAddress(), deserializedUser.getEmailAddress());
		Assert.assertEquals(expected.getShortName(), deserializedUser.getShortName());
		Assert.assertEquals(expected.getPassword(), deserializedUser.getPassword());
		Assert.assertEquals(expected.getCreatedAt(), deserializedUser.getCreatedAt());
		Assert.assertEquals(expected.getLastModifiedAt(), deserializedUser.getLastModifiedAt());
	}
}