package at.spot.core.infrastructure.support.init;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
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

import at.spot.core.CoreInit;
import at.spot.core.infrastructure.exception.BootstrapException;
import at.spot.core.infrastructure.spring.ItemTypeAnnotationProcessor;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.core.support.util.ClassUtil;
import at.spot.core.support.util.PropertiesUtil;
import at.spot.instrumentation.DynamicInstrumentationLoader;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * This is the main entry point to startup a spOt instance. First the classpath
 * is scanned for {@link ModuleInit} implementations (there should be one for
 * each spot module) which it then tries to load. The init classes take care of
 * all necessary initialization for their module.
 */
@SuppressFBWarnings("BC_UNCONFIRMED_CAST_OF_RETURN_VALUE")
public class Bootstrap extends SpringApplicationBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(Bootstrap.class);
	public static final long MAIN_THREAD_ID = Thread.currentThread().getId();

	static {
		DynamicInstrumentationLoader.initialize();
	}

	private Bootstrap() {
		Registry.setMainThread(Thread.currentThread());
	}

	protected static SpringApplicationBuilder build(final Class<? extends ModuleInit> initClass) {
		Registry.setMainClass(initClass);
		return new Bootstrap().sources(initClass).registerShutdownHook(true).bannerMode(Mode.OFF);
	}

	public static ConfigurableApplicationContext bootstrap(final Class<? extends ModuleInit> configuration,
			final String[] modelScanPaths) {
		final SpringApplicationBuilder builder = build(CoreInit.class);

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
		return builder.child(configuration).web(WebApplicationType.SERVLET).registerShutdownHook(true)
				.bannerMode(Mode.OFF).run();
	}

	/**
	 * This is the main entry point for the bootstrap mechanism.
	 * 
	 * @param options
	 */
	public static void bootstrap(final BootstrapOptions options) {
		setDefaultLocale();
		setLogSettings();

		final SpringApplicationBuilder builder = build(options.getInitClass()).web(WebApplicationType.NONE);

		// load external properties into builder
		loadConfiguration(builder, options);

		// load external spring configs into builder
		loadSpringConfiguration(builder, options);

		LOG.info("Bootstrapping done.");

		builder.run();
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

	/**
	 * Load the configuration properties passed via command line into the spring
	 * application builder.
	 * 
	 * @param builder
	 * @param options
	 * @return
	 * @throws IOException
	 */
	protected static void loadConfiguration(final SpringApplicationBuilder builder, final BootstrapOptions options) {
		// load application config, possibly override module configs
		if (StringUtils.isNotBlank(options.getAppConfigFile())) {
			final Properties prop = PropertiesUtil.loadPropertiesFromFile(options.getAppConfigFile());

			builder.properties(prop);
		}
	}

	/**
	 * Inject a bean definition using a {@link BeanDefinitionReader}. This is
	 * necessary, so that the spring context of this module can be merged with the
	 * parent context.
	 * 
	 * @param parentContext
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
	 * @return
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

		final CommandLineParser parser = new DefaultParser();
		final CommandLine cmd = parser.parse(options, args);

		if (cmd.hasOption("p")) {
			ret.setAppConfigFile(cmd.getOptionValue("p"));
		}

		if (cmd.hasOption("s")) {
			ret.setSpringConfigFile(cmd.getOptionValue("s"));
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

	/********************************************************************************************
	 * MAIN ENTRY POINT
	 *******************************************************************************************/

	public static void main(final String[] args) throws Exception {
		bootstrap(parseCommandLine(args));
	}
}
