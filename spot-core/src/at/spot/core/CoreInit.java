package at.spot.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.init.ModuleInit;
import at.spot.core.infrastructure.service.EventService;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.model.user.User;
import at.spot.core.model.user.UserGroup;
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
public class CoreInit extends ModuleInit {

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected LoggingService loggingService;

	@Autowired
	protected PersistenceService persistenceService;

	@Autowired
	protected EventService eventService;

	@Autowired
	protected QueryService queryService;

	@Override
	public void injectBeanDefinition(BeanDefinitionRegistry parentContext) {
		BeanDefinitionReader reader = new XmlBeanDefinitionReader(parentContext);
		reader.loadBeanDefinitions("spring-core.xml");
	}

	public void run() {
		long start = System.currentTimeMillis();

		// first clear storage, then we start our test
		// persistenceService.clearDataStorage();

		try {

			List<User> users = new ArrayList<>();

			UserGroup group = modelService.create(UserGroup.class);
			group.name = "tester group";
			group.uid = "test-group";

			User user1 = modelService.create(User.class);
			User user2 = modelService.create(User.class);

			user1.uid = "user-1";
			user2.uid = "user-2";

			user1.groups.add(group);
			user2.groups.add(group);

			users.add(user1);
			users.add(user2);

			modelService.saveAll(users);

			for (int i = 1; i < 10000; i++) {
				if (i > 0 && i % 50 == 0) {
					long duration = System.currentTimeMillis() - start;

					if (duration >= 1000) {
						// loggingService.debug("Created " + i + " users (" + i
						// / (duration / 1000) + " items/s )");
					}
				}

				User user = modelService.create(User.class);
				user.name = "test-" + i;
				user.uid = user.name;

				user.groups.add(group);

				users.add(user);
			}

			modelService.saveAll(users);

			Map<String, Comparable<?>> criteria = new HashMap<>();
			criteria.put("uid", "user-1");

			User test99 = modelService.get(User.class, criteria);

			user1.groups.get(0).uid = "abc";

			// iterate over all children and check dirty flag
			modelService.saveAll(user1);

			user1 = modelService.get(User.class, user1.pk);
			user2 = modelService.get(User.class, user2.pk);

			// System.out.println(user1.groups.get(0).uid);
			// System.out.println(user2.groups.get(0).uid);

			modelService.refresh(user2);

			// System.out.println(user2.groups.get(0).uid);

			// Query query = Query.select(User.class)
			// .where(Condition.startsWith("groups.uid", "test",
			// true).or(Condition.equals("uid", "User-1", true)))
			// .build();
			//
			// QueryResult result = queryService.query(query);

			// System.out.print("");
		} catch (Exception e) {
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

	@Log(message = "Starting spOt core ...")
	@Override
	public void initialize() {
		setupTypeInfrastrucutre();
		runMigrateScripts();

		// this is just for testing
//		run();
	}

	@Log(message = "Running data migration scripts ...")
	protected void runMigrateScripts() {
		// not yet implemented
	}

	@Log(message = "Setting up type registry ...")
	protected void setupTypeInfrastrucutre() {
		typeService.registerTypes();
		persistenceService.initDataStorage();
	}
}
