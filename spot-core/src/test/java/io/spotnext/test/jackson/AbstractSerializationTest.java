package io.spotnext.test.jackson;

import java.io.IOException;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.core.testing.SpotSpringExtension;
import io.spotnext.itemtype.core.catalog.Catalog;
import io.spotnext.itemtype.core.catalog.CatalogVersion;
import io.spotnext.itemtype.core.user.User;
import io.spotnext.itemtype.core.user.UserGroup;

@ExtendWith(SpotSpringExtension.class)
public abstract class AbstractSerializationTest {

	@Mock
	TypeService typeService;
	@Mock
	ApplicationContext context;

	public AbstractSerializationTest() {
		super();
	}

	@BeforeEach
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

	protected Catalog mockCatalog(int i) {
		Catalog catalog = new Catalog();
		catalog.setUid("testCatalog");
		catalog.versions = new HashSet<>();

		var cvs = new HashSet<CatalogVersion>();

		for (int b = 0; b < i; b++) {
			CatalogVersion cv = new CatalogVersion();
			cv.setUid("version" + b);
			cvs.add(cv);
		}

		catalog.setVersions(cvs);

		return catalog;
	}

	protected void assertUser(User expected, User deserializedUser) {
		Assertions.assertEquals(expected.getGroups().size(), deserializedUser.getGroups().size());
		Assertions.assertEquals(expected.getId(), deserializedUser.getId());
		Assertions.assertEquals(expected.getUid(), deserializedUser.getUid());
		Assertions.assertEquals(expected.getEmailAddress(), StringUtils.trimToNull(deserializedUser.getEmailAddress()));
		Assertions.assertEquals(expected.getShortName(), deserializedUser.getShortName());
		Assertions.assertEquals(expected.getPassword(), StringUtils.trimToNull(deserializedUser.getPassword()));
		Assertions.assertEquals(expected.getCreatedAt(), deserializedUser.getCreatedAt());
		Assertions.assertEquals(expected.getLastModifiedAt(), deserializedUser.getLastModifiedAt());
	}

}