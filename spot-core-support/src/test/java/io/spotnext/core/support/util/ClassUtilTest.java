package io.spotnext.core.support.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import io.spotnext.core.support.util.data.TestClass;
import io.spotnext.support.util.ClassUtil;

public class ClassUtilTest {

	@Test
	public void testInstantiationOfStaticInnerClass() {
		Optional<StaticInnerClass> instance = ClassUtil.instantiate(StaticInnerClass.class);

		Assert.assertTrue(instance.isPresent());
	}

	@Test
	public void testInstantiationOfInnerClass() {
		Optional<InnerClass> instance = ClassUtil.instantiate(InnerClass.class);

		Assert.assertTrue(instance.isPresent());
	}

	@Test
	public void testInstantiationOfClass() {
		Optional<TestClass> instance = ClassUtil.instantiate(TestClass.class);

		Assert.assertTrue(instance.isPresent());
	}

	@Test
	public void testVisiteFields() {
		User user = new User("test-user", new UserGroup("test-group", new Address("test-group-address"),
				new Address("test-business-address")), new Address("test-street"));

		List<Item> foundObjects = new ArrayList<>();

		ClassUtil.visitFields(user, (field) -> Item.class.isAssignableFrom(field.getType()),
				(field, object) -> foundObjects.add((Item) object), true);

		Assert.assertEquals(4, foundObjects.size());
	}

	private static class StaticInnerClass {
		//
	}

	private class InnerClass {
		//
	}

	private abstract static class Item {

	}

	private static class Address extends Item {
		private String street;

		public Address(String street) {
			this.street = street;
		}
	}

	private abstract static class Principal extends Item {
		private String id;

		public Principal(String id) {
			this.id = id;
		}
	}

	private static class User extends Principal {
		private UserGroup userGroup;
		private Address address;
		private Address address2 = null;

		public User(String id, UserGroup userGroup, Address address) {
			super(id);
			this.userGroup = userGroup;
			this.address = address;
		}
	}

	private static class UserGroup extends Principal {
		private Address businessAddress;
		private Address address;

		public UserGroup(String id, Address businessAddress, Address address) {
			super(id);
			this.address = address;
			this.businessAddress = businessAddress;
		}
	}
}
