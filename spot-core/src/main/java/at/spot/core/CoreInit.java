package at.spot.core;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
import at.spot.core.infrastructure.support.init.ModuleInit;
import at.spot.core.persistence.exception.ModelNotUniqueException;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.persistence.service.QueryService;
import at.spot.itemtype.core.user.User;
import at.spot.itemtype.core.user.UserGroup;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This is the main entry point for the application. After the application has
 * been initialized, it will call {@link CoreInit#run()}. Then the shell is
 * being loaded.
 */
@ImportResource("classpath:/core-spring.xml")
@PropertySource("classpath:/core.properties")
@EnableAsync
@EnableTransactionManagement
@EnableScheduling
@EnableJpaAuditing
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

	@SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
	@Log(message = "Importing test data ...")
	public void importTestData() {
		StopWatch watch = StopWatch.createStarted();

		List<User> users = new ArrayList<>();

		UserGroup userGroup = modelService.create(UserGroup.class);
		userGroup.setId("testUserGroup");
		userGroup.setShortName("Test user group");

		modelService.save(userGroup);

		for (int i = 0; i < 10; i++) {
			final User user = modelService.create(User.class);
			user.setId("user-" + UUID.randomUUID());
			user.setEmailAddress("test@test.at");
			user.setPassword("test1234");
			user.setShortName("Test user");

			userGroup.getMembers().add(user);

			users.add(user);
		}

		modelService.saveAll(users);
		watch.stop();
		loggingService.info("Test data import took " + watch.getNanoTime() / 1000 / 1000 + " sec");
	}

	/*
	 * STARTUP FUNCTIONALITY
	 */

	@Override
	@Log(message = "Initializing system ...")
	protected void initialize() throws ModuleInitializationException {
		runMigrateScripts();
		importInitialData();

		// this is just for testing
		importTestData();
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
			admin.setId(adminUserName);
			admin.setPassword(adminPassword);

			try {
				modelService.save(admin);

				loggingService.debug("Created admin user.");
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
