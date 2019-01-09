package io.spotnext.test.jackson;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.core.testing.SpotJunitRunner;
import io.spotnext.itemtype.core.catalog.Catalog;
import io.spotnext.itemtype.core.catalog.CatalogVersion;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

@RunWith(SpotJunitRunner.class)
public abstract class AbstractSerializationTest {

	@Mock
	TypeService typeService;
	@Mock
	ApplicationContext context;

	Random random = new Random();

	public AbstractSerializationTest() {
		super();
	}

	@Before
	public void setup() throws IOException, UnknownTypeException, ClassNotFoundException {
		MockitoAnnotations.initMocks(this);

		Mockito.when(context.getBean("typeService")).thenReturn(typeService);
		Mockito.when(context.getBean(TypeService.class)).thenReturn(typeService);
		Registry.instance().setApplicationContext(context);

		Mockito.when(typeService.getClassForTypeCode(User.TYPECODE)).thenReturn((Class) User.class);
		Mockito.when(typeService.getClassForTypeCode(UserGroup.TYPECODE)).thenReturn((Class) UserGroup.class);
		Mockito.when(typeService.getClassForTypeCode(Catalog.TYPECODE)).thenReturn((Class) Catalog.class);
		Mockito.when(typeService.getClassForTypeCode(CatalogVersion.TYPECODE)).thenReturn((Class) CatalogVersion.class);

		Mockito.when(typeService.getTypeCodeForClass(User.class)).thenReturn(User.TYPECODE);
		Mockito.when(typeService.getTypeCodeForClass(UserGroup.class)).thenReturn(UserGroup.TYPECODE);
		Mockito.when(typeService.getTypeCodeForClass(Catalog.class)).thenReturn(Catalog.TYPECODE);
		Mockito.when(typeService.getTypeCodeForClass(CatalogVersion.class)).thenReturn(CatalogVersion.TYPECODE);
	}

	protected User mockUser() {
		User user = new User();
		user.set("id", random.nextLong());
		user.setUid("userID");
		user.setShortName("user");
		user.setGroups(new HashSet<>());

		UserGroup group1 = new UserGroup();
		group1.set("id", random.nextLong());
		group1.setUid("group1ID");
		group1.setShortName("group1");
		group1.setMembers(new HashSet<>());
		group1.getMembers().add(user);

		UserGroup group2 = new UserGroup();
		group2.set("id", random.nextLong());
		group2.setUid("group2ID");
		group2.setShortName("group2");
		group2.setMembers(new HashSet<>());
		group2.getMembers().add(user);

		user.groups.add(group1);
		user.groups.add(group2);

		return user;
	}

	protected Catalog mockCatalog(int i) {
		Catalog catalog = new Catalog();
		catalog.set("id", random.nextLong());
		catalog.setUid("testCatalog");
		catalog.versions = new HashSet<>();

		var cvs = new HashSet<CatalogVersion>();

		for (int b = 0; b < i; b++) {
			CatalogVersion cv = new CatalogVersion();
			cv.set("id", random.nextLong());
			cv.setUid("version" + b);
			cvs.add(cv);
		}

		catalog.setVersions(cvs);

		return catalog;
	}

	protected void assertUser(User expected, User deserializedUser) {
		Assert.assertEquals(expected.getGroups().size(), deserializedUser.getGroups().size());
		Assert.assertEquals(expected.getId(), deserializedUser.getId());
		Assert.assertEquals(expected.getUid(), deserializedUser.getUid());
		Assert.assertEquals(expected.getEmailAddress(), deserializedUser.getEmailAddress());
		Assert.assertEquals(expected.getShortName(), deserializedUser.getShortName());
		Assert.assertEquals(expected.getPassword(), deserializedUser.getPassword());
		Assert.assertEquals(expected.getCreatedAt(), deserializedUser.getCreatedAt());
		Assert.assertEquals(expected.getLastModifiedAt(), deserializedUser.getLastModifiedAt());
	}

}