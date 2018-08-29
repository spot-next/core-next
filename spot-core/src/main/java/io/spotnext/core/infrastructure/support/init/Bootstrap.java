package io.spotnext.core.infrastructure.support.init;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.core.CoreInit;
import io.spotnext.core.infrastructure.exception.BootstrapException;
import io.spotnext.core.infrastructure.spring.ItemTypeAnnotationProcessor;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.core.support.util.ClassUtil;
import io.spotnext.core.support.util.PropertiesUtil;

/**
 * This is the main entry point to startup a spOt instance. First the classpath
 * is scanned for
 * {@link io.spotnext.core.infrastructure.support.init.ModuleInit}
 * implementations (there should be one for each spot module) which it then
 * tries to load. The init classes take care of all necessary initialization for
 * their module.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
public class Bootstrap {
	private static final Logger LOG = LoggerFactory.getLogger(Bootstrap.class);
	/** Constant <code>MAIN_THREAD_ID=Thread.currentThread().getId()</code> */
	public static final long MAIN_THREAD_ID = Thread.currentThread().getId();

	private final SpringApplicationBuilder builder;

	private Bootstrap() {
		Registry.setMainThread(Thread.currentThread());
		builder = new SpringApplicationBuilder();
	}

	/**
	 * <p>
	 * sources.
	 * </p>
	 *
	 * @param sources a {@link java.lang.Class} object.
	 * @return a {@link org.springframework.boot.builder.SpringApplicationBuilder}
	 *         object.
	 */
	public SpringApplicationBuilder sources(final Class<?>... sources) {
		return builder.sources(sources);
	}

	protected static SpringApplicationBuilder build(final Class<? extends ModuleInit> initClass) {
		Registry.setMainClass(initClass);
		return new Bootstrap().sources(initClass).registerShutdownHook(true).bannerMode(Mode.CONSOLE);
	}

	/**
	 * <p>
	 * bootstrap.
	 * </p>
	 *
	 * @param configuration  a {@link java.lang.Class} object.
	 * @param modelScanPaths an array of {@link java.lang.String} objects.
	 * @param args           an array of {@link java.lang.String} objects.
	 * @return a {@link org.springframework.boot.builder.SpringApplicationBuilder}
	 *         object.
	 * @throws io.spotnext.core.infrastructure.exception.BootstrapException if any.
	 */
	public static SpringApplicationBuilder bootstrap(final Class<? extends ModuleInit> configuration,
			final String[] modelScanPaths, final String[] args) throws BootstrapException {

		SpringApplicationBuilder builder;
		try {
			builder = bootstrap(parseCommandLine(args));
		} catch (ParseException e) {
			throw new BootstrapException("Could not parse command line arguments", e);
		}

		// inject the default spring application.properties into the parent (=CoreInit),
		// so that the child can override properties
		URL applicationProperties = configuration.getClass().getResource("/application.properties");
		try {
			loadConfigurationProperties(builder, applicationProperties);
		} catch (URISyntaxException e) {
			throw new BootstrapException("Could not resolve default spring application.properties file", e);
		}

		builder.initializers(new ApplicationContextInitializer<ConfigurableApplicationContext>() {
			@Override
			public void initialize(final ConfigurableApplicationContext applicationContext) {
				applicationContext.getBeanFactory()
						.addBeanPostProcessor(new ItemTypeAnnotationProcessor(applicationContext.getBeanFactory()));

				if (applicationContext instanceof AnnotationConfigApplicationContext) {
					for (final String path : modelScanPaths) {
						((AnnotationConfigApplicationContext) applicationContext).scan(path);
					}
				} else {
					LOG.warn("Could not inject model scan paths");
				}
			};
		});

		// override default mainClass
		Registry.setMainClass(configuration);
		return builder.child(configuration).registerShutdownHook(true).bannerMode(Mode.OFF);
	}

	/**
	 * This is the main entry point for the bootstrap mechanism.
	 *
	 * @param options a
	 *                {@link io.spotnext.core.infrastructure.support.init.BootstrapOptions}
	 *                object.
	 * @return a {@link org.springframework.boot.builder.SpringApplicationBuilder}
	 *         object.
	 */
	public static SpringApplicationBuilder bootstrap(final BootstrapOptions options) {
		setDefaultLocale();
		setLogSettings();

		final SpringApplicationBuilder builder = build(options.getInitClass()).web(WebApplicationType.NONE);

		// load external properties into builder
		loadConfigurationProperties(builder, options.getAppConfigFile());

		// load external spring configs into builder
		loadSpringConfiguration(builder, options);

		loadCommandLineArgsIntoSpringContext(builder, options);

		LOG.info("Bootstrapping done.");

		return builder;
	}

	/**
	 * Adds the parsed command line args regarding type system initialization and
	 * import to the spring properties.
	 */
	protected static void loadCommandLineArgsIntoSpringContext(final SpringApplicationBuilder builder,
			final BootstrapOptions options) {

		builder.addCommandLineProperties(true);

		final Map<String, Object> props = new HashMap<>();
		props.put("core.setup.typesystem.initialize", options.isInitializeTypeSystem());
		props.put("core.setup.typesystem.update", options.isUpdateTypeSystem());
		props.put("core.setup.typesystem.clean", options.isCleanTypeSystem());
		props.put("core.setup.import.initialdata", options.isImportInitialData());
		props.put("core.setup.import.sampledata", options.isImportSampleData());

		builder.properties(props);
	}

	protected static List<ModuleConfig> loadModuleConfig(final BootstrapOptions options) {
		final List<ModuleConfig> moduleConfigs = new LinkedList<>();

		moduleConfigs.add(ClassUtil.getAnnotation(options.getInitClass(), ModuleConfig.class));

		for (final Class<?> c : ClassUtil.getAllSuperClasses(options.getInitClass(), ModuleInit.class, true, false)) {
			final ModuleConfig conf = ClassUtil.getAnnotation(c, ModuleConfig.class);

			if (conf != null) {
				moduleConfigs.add(conf);
			}
		}

		// reverse the order, this is necessary to load the configs in the
		// correct order
		Collections.reverse(moduleConfigs);

		return moduleConfigs;
	}

	protected static void loadConfigurationProperties(final SpringApplicationBuilder builder, URL propertiesFile)
			throws URISyntaxException {

		if (propertiesFile != null) {
			loadConfigurationProperties(builder, new File(propertiesFile.toURI()).getAbsolutePath());
		}
	}

	/**
	 * Load the configuration properties passed via command line into the spring
	 * application builder.
	 * 
	 * @param builder
	 * @param options
	 * @throws IOException
	 */
	protected static void loadConfigurationProperties(final SpringApplicationBuilder builder, String propertiesFile) {
		// load application config, possibly override module config's
		if (StringUtils.isNotBlank(propertiesFile)) {
			final Properties prop = PropertiesUtil.loadPropertiesFromFile(propertiesFile);

			builder.properties(prop);
		}
	}

	/**
	 * Inject a bean definition using a {@link BeanDefinitionReader}. This is
	 * necessary, so that the spring context of this module can be merged with the
	 * parent context.
	 */
	protected static void loadSpringConfiguration(final SpringApplicationBuilder builder,
			final BootstrapOptions options) {

		builder.initializers(new ApplicationContextInitializer<ConfigurableApplicationContext>() {
			@Override
			public void initialize(final ConfigurableApplicationContext context) {
				if (context instanceof BeanDefinitionRegistry) {
					final BeanDefinitionReader reader = new XmlBeanDefinitionReader((BeanDefinitionRegistry) context);

					// get application spring config
					if (StringUtils.isNotBlank(options.getSpringConfigFile())) {
						reader.loadBeanDefinitions(options.getSpringConfigFile());
					}
				} else {
					LOG.warn("Can't inject spring configuration that has been passed by command line.");
				}
			}
		});
	}

	/**
	 * Get command line arguments, eg. for getting the custom *.properties file.
	 * 
	 * @param args
	 * @throws ParseException
	 * @throws BootstrapException
	 */
	@SuppressWarnings("unchecked")
	protected static BootstrapOptions parseCommandLine(final String... args) throws ParseException, BootstrapException {
		final BootstrapOptions ret = new BootstrapOptions();

		final Options options = new Options();
		options.addOption(Option.builder("appconfig").longOpt("p").desc("application the properties file").hasArg()
				.argName("appconfig").build());
		options.addOption(Option.builder("springconfig").longOpt("s").desc("spring config file").hasArg()
				.argName("springconfig").build());
		options.addOption(Option.builder("initclass").longOpt("i").desc("application the properties file").hasArg()
				.argName("initclass").build());

		// init and update type system commands
		options.addOption(Option.builder("initializetypesystem").longOpt("init") //
				.optionalArg(true).desc("initialize the type system (removes all all data in the database)").build());
		options.addOption(Option.builder("importinitialdata").longOpt("importinitialdata") //
				.optionalArg(true).desc("Imports the initial data").build());
		options.addOption(Option.builder("importsampledata").longOpt("importsampledata") //
				.optionalArg(true).desc("Imports the sample data").build());
		options.addOption(Option.builder("updatetypesystem").longOpt("updatetypesystem") //
				.optionalArg(true).desc("updates the type system").build());
		options.addOption(Option.builder("cleantypesystem").longOpt("cleantypesystem") //
				.optionalArg(true).desc("Removes unused typesystem data").build());

		final CommandLineParser parser = new DefaultParser();
		final CommandLine cmd = parser.parse(options, args);

		if (cmd.hasOption("p")) {
			ret.setAppConfigFile(cmd.getOptionValue("p"));
		}

		if (cmd.hasOption("s")) {
			ret.setSpringConfigFile(cmd.getOptionValue("s"));
		}

		if (cmd.hasOption("initializetypesystem")) {
			ret.setInitializeTypeSystem(true);
		}
		if (cmd.hasOption("updatetypesystem")) {
			ret.setUpdateTypeSystem(true);
		}
		if (cmd.hasOption("cleantypesystem")) {
			ret.setCleanTypeSystem(true);
		}
		if (cmd.hasOption("importinitialdata")) {
			ret.setImportInitialData(true);
		}
		if (cmd.hasOption("importsampledata")) {
			ret.setImportSampleData(true);
		}

		Class<? extends ModuleInit> initClass = CoreInit.class;

		if (cmd.hasOption("i")) {
			final String initClassName = cmd.getOptionValue("i");

			try {
				initClass = (Class<? extends ModuleInit>) Class.forName(initClassName);
			} catch (ClassNotFoundException | ClassCastException e) {
				throw new BootstrapException(String.format("Could not load init class %s", initClassName));
			}
		} else {
			ret.setInitClass(CoreInit.class);
		}

		ret.setInitClass(initClass);

		return ret;
	}

	/**
	 * Sets org.reflections logging to warnings, as we scan all package paths. This
	 * causes a lot of debug messages being logged.
	 */
	protected static void setLogSettings() {
		System.setProperty("org.slf4j.simpleLogger.log.org.reflections", "warn");
	}

	/**
	 * Sets default locale, overriding the system default.
	 */
	protected static void setDefaultLocale() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);
	}

	/**
	 ******************************************************************************************
	 * MAIN ENTRY POINT
	 ******************************************************************************************
	 *
	 * @param args an array of {@link java.lang.String} objects.
	 * @throws java.lang.Exception if any.
	 */
	public static void main(final String[] args) throws Exception {
		bootstrap(parseCommandLine(args)).run();
	}
}
