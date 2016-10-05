package at.spot.core.shell;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.shell.Bootstrap;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.persistence.service.PersistenceService;

/**
 * This is the main entry point for the application. After the application has
 * been initialized, it will call {@link CoreInit#run()}. Then the shell is
 * being loaded.
 */
@Service
public class CoreInit {

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected PersistenceService persistenceService;

	public static void main(String[] args) throws Exception {
		new ClassPathXmlApplicationContext("spring.xml").close();
	}

	public void run() {
		// for (int i = 0; i < 100; i++) {
		// User user = getModelService().create(User.class);
		// user.name = "test-" + i;
		// user.uid = user.name;
		//
		// try {
		// getModelService().save(user);
		// } catch (ModelSaveException e) {
		// getLoggingService().exception(e.getMessage());
		// }
		// }
		//
		// try {
		// User loadedUser = getModelService().get(User.class, 0l);
		// getLoggingService().info("loaded user again");
		// } catch (ModelNotFoundException e) {
		// getLoggingService().exception(e.getMessage());
		// }

		// getPersistenceService().saveDataStorage();

	}

	/*
	 * STARTUP FUNCTIONALITY
	 */

	public void initSpring() {

	}

	@PostConstruct
	public void init() throws Exception {
		// initSpring();
		setupTypeInfrastrucutre();

		run();

		startShell();
	}

	@Log(logLevel = LogLevel.DEBUG, before = true, measureTime = false)
	protected void setupTypeInfrastrucutre() {
		typeService.registerTypes();
		persistenceService.initDataStorage();
	}

	@Log(logLevel = LogLevel.DEBUG, before = true, measureTime = false)
	public void startShell(String... args) throws Exception {
		Bootstrap.main(args);
	}
}
