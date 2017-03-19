package at.spot.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import at.spot.core.constant.CoreConstants;
import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.exception.ModelSaveException;
import at.spot.core.infrastructure.exception.ModelValidationException;
import at.spot.core.infrastructure.exception.ModuleInitializationException;
import at.spot.core.infrastructure.service.ConfigurationService;
import at.spot.core.infrastructure.service.EventService;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.service.UserService;
import at.spot.core.infrastructure.support.init.ModuleConfig;
import at.spot.core.infrastructure.support.init.ModuleInit;
import at.spot.core.model.user.Address;
import at.spot.core.model.user.AddressType;
import at.spot.core.model.user.User;
import at.spot.core.model.user.UserGroup;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.persistence.service.QueryService;

/**
 * This is the main entry point for the application. After the application has
 * been initialized, it will call {@link CoreInit#run()}. Then the shell is
 * being loaded.
 */
@EnableAsync
@EnableScheduling
@Service
// @Order(value = 1)
@ModuleConfig(moduleName = "core", modelPackagePaths = {
		"at.spot.core.model" }, appConfigFile = "core.properties", springConfigFile = "core-spring.xml")
public class CoreInit extends ModuleInit {

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected ConfigurationService configurationService;

	@Autowired
	protected UserService<User, UserGroup> userService;

	@Autowired
	protected LoggingService loggingService;

	@Autowired
	protected PersistenceService persistenceService;

	@Autowired
	protected EventService eventService;

	@Autowired
	protected QueryService queryService;

	public void run() {
		final long start = System.currentTimeMillis();

		// first clear storage, then we start our test
		// persistenceService.clearDataStorage();

		try {
			final User user1 = modelService.create(User.class);
			user1.uid = "test1";

			final UserGroup userGroup1 = modelService.create(UserGroup.class);
			userGroup1.uid = "group1";
			final UserGroup userGroup2 = modelService.create(UserGroup.class);
			userGroup2.uid = "group2";

			modelService.saveAll(user1, userGroup1, userGroup2);

			final AddressType at = modelService.create(AddressType.class);
			at.code = "private";

			final Address address1 = modelService.create(Address.class);
			address1.type = at;
			address1.owner = user1;

			modelService.save(address1);

			modelService.refresh(user1);
			System.out.println(user1.addresses);

			user1.addresses.remove(address1);
			modelService.save(user1);

			System.out.println(user1.addresses);
			System.out.println("");

			// final List<User> users = new ArrayList<>();
			//
			// final UserGroup group = modelService.create(UserGroup.class);
			// group.name = "tester group";
			// group.uid = "test-group";
			//
			// User user1 = modelService.create(User.class);
			// User user2 = modelService.create(User.class);
			//
			// user1.uid = "user-1";
			// user2.uid = "user-2";
			//
			// user1.groups.add(group);
			// user2.groups.add(group);
			//
			// users.add(user1);
			// users.add(user2);
			//
			// modelService.saveAll(users);
			//
			// for (int i = 1; i < 10000; i++) {
			// if (i > 0 && i % 50 == 0) {
			// final long duration = System.currentTimeMillis() - start;
			//
			// if (duration >= 1000) {
			// // loggingService.debug("Created " + i + " users (" + i
			// // / (duration / 1000) + " items/s )");
			// }
			// }
			//
			// final User user = modelService.create(User.class);
			// user.name = "test-" + i;
			// user.uid = user.name;
			//
			// user.groups.add(group);
			//
			// users.add(user);
			// }
			//
			// modelService.saveAll(users);
			//
			// final Map<String, Comparable<?>> criteria = new HashMap<>();
			// criteria.put("uid", "user-1");
			//
			// final User test99 = modelService.get(User.class, criteria);
			//
			// user1.groups.get(0).uid = "abc";
			//
			// // iterate over all children and check dirty flag
			// modelService.saveAll(user1);
			//
			// user1 = modelService.get(User.class, user1.pk);
			// user2 = modelService.get(User.class, user2.pk);

			// System.out.println(user1.groups.get(0).uid);
			// System.out.println(user2.groups.get(0).uid);

			// modelService.refresh(user2);

			// System.out.println(user2.groups.get(0).uid);

			// Query query = Query.select(User.class)
			// .where(Condition.startsWith("groups.uid", "test",
			// true).or(Condition.equals("uid", "User-1", true)))
			// .build();
			//
			// QueryResult result = queryService.query(query);

			// System.out.print("");
		} catch (final Exception e) {
			loggingService.exception(e.getMessage(), e);
		}

		// try {
		// User loadedUser = modelService.get(User.class, 0l);
		// loggingService.info("loaded user again");
		// } catch (ModelNotFoundException e) {
		// loggingService.exception(e.getMessage());
		// }

		persistenceService.saveDataStorage();

	}

	/*
	 * STARTUP FUNCTIONALITY
	 */

	@Log(message = "Starting spOt core ... ")
	@Override
	public void initialize() throws ModuleInitializationException {
		setupTypeInfrastrucutre();
		importInitialData();
		runMigrateScripts();

		// this is just for testing
		// run();
	}

	@Log(message = "Setting up type registry ...")
	protected void setupTypeInfrastrucutre() {
		typeService.registerTypes();
		persistenceService.initDataStorage();
	}

	@Log(message = "Importing initial data ...")
	protected void importInitialData() throws ModuleInitializationException {
		final String adminUserName = configurationService.getString(CoreConstants.CONFIG_KEY_DEFAULT_ADMIN_USERNAME,
				CoreConstants.DEFAULT_ADMIN_USERNAME);
		final String adminPassword = configurationService.getString(CoreConstants.CONFIG_KEY_DEFAULT_ADMIN_PASSWORD,
				CoreConstants.DEFAULT_ADMIN_PASSWORD);

		User admin = userService.getUser(adminUserName);

		if (admin == null) {
			admin = modelService.create(User.class);
			admin.uid = adminUserName;
			admin.password = adminPassword;

			try {
				modelService.save(admin);
			} catch (ModelSaveException | ModelNotUniqueException | ModelValidationException e) {
				throw new ModuleInitializationException("Couln't create admin user account.", e);
			}
		}
	}

	@Log(message = "Running data migration scripts ...")
	protected void runMigrateScripts() {
		// not yet implemented
	}

}
