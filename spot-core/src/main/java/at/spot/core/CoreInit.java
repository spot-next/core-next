package at.spot.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import at.spot.core.infrastructure.annotation.logging.Log;
import at.spot.core.infrastructure.exception.ImportException;
import at.spot.core.infrastructure.exception.ModuleInitializationException;
import at.spot.core.infrastructure.service.EventService;
import at.spot.core.infrastructure.service.ImportService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.service.UserService;
import at.spot.core.infrastructure.support.init.ModuleInit;
import at.spot.core.persistence.service.PersistenceService;
import at.spot.core.persistence.service.QueryService;
import at.spot.itemtype.core.beans.ImportConfiguration;
import at.spot.itemtype.core.enumeration.ImportFormat;
import at.spot.itemtype.core.user.User;
import at.spot.itemtype.core.user.UserGroup;
import ch.qos.logback.core.util.CloseUtil;
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

	@Resource
	protected ImportService importService;

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected ModelService modelService;

	@Autowired
	protected UserService<User, UserGroup> userService;

	@Autowired
	protected PersistenceService persistenceService;

	@Autowired
	protected EventService eventService;

	@Autowired
	protected QueryService queryService;

	@SuppressFBWarnings("DLS_DEAD_LOCAL_STORE")
	@Log(message = "Importing test data ...")
	protected void importTestData() {
		final StopWatch watch = StopWatch.createStarted();

		final List<User> users = new ArrayList<>();

		final UserGroup userGroup = modelService.create(UserGroup.class);
		userGroup.setId("testUserGroup");
		userGroup.setShortName("Test user group");

		modelService.save(userGroup);

		for (int i = 0; i < 100; i++) {
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
	@Log(message = "Initializing core")
	protected void initialize() throws ModuleInitializationException {
		//
	}

	@Override
	@Log(message = "Importing initial data ...", measureTime = true)
	protected void importInitialData() throws ModuleInitializationException {
		try {
			importScript("/data/initial/countries.impex", "Importing countries");
			importScript("/data/initial/languages.impex", "Importing languages");
			importScript("/data/initial/currencies.impex", "Importing currencies");
			importScript("/data/initial/users.impex", "Importing users");
			importScript("/data/initial/catalogs.impex", "Importing catalogs");
		} catch (final ImportException e) {
			loggingService.warn("Could not import initial data: " + e.getMessage());
		}
	}

	@SuppressFBWarnings(value = "OBL_UNSATISFIED_OBLIGATION", justification = "Stream is closed in ImportService")
	@Override
	@Log(message = "Importing sample data ...", measureTime = true)
	protected void importSampleData() throws ModuleInitializationException {
		try {
			importScript("/data/sample/users.impex", "Importing sample users");
			importScript("/data/sample/medias.impex", "Importing sample medias");
		} catch (final ImportException e) {
			loggingService.warn("Could not import initial data: " + e.getMessage());
		}
	}

	private void importScript(final String path, final String logMessage) throws ImportException {
		loggingService.debug(logMessage);

		InputStream stream = null;
		try {
			final ImportConfiguration conf = new ImportConfiguration();
			conf.setIgnoreErrors(true);
			conf.setScriptIdentifier(path);

			stream = CoreInit.class.getResourceAsStream(conf.getScriptIdentifier());
			importService.importItems(ImportFormat.ImpEx, conf, stream);
		} finally {
			CloseUtil.closeQuietly(stream);
		}
	}
}
