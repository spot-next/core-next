package at.spot.core.infrastructure.support.init;

import java.io.IOException;
import java.util.Arrays;
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
import org.springframework.beans.factory.access.BootstrapException;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;

import at.spot.core.CoreInit;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.core.support.util.ClassUtil;
import at.spot.core.support.util.PropertiesUtil;
import at.spot.core.support.util.SpringUtil;
import at.spot.core.support.util.SpringUtil.BeanScope;

/**
 * This is the main entry point to startup a spOt instance. First the classpath
 * is scanned for {@link ModuleInit} implementations (there should be one for
 * each spot module) which it then tries to load. The init classes take care of
 * all necessary initialization for their module.
 */
public class Bootstrap {
	public static final long MAIN_THREAD_ID = Thread.currentThread().getId();

	public static void main(final String[] args) throws Exception {
		bootstrap(parseCommandLine(args));
	}

	/**
	 * This is the main entry point for the bootstrap mechanism.
	 * 
	 * @param options
	 */
	public static void bootstrap(final BootstrapOptions options) {
		setDefaultLocale();
		setLogSettings();

		// create a generic spring context
		final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

		// inject spring context into Registry
		Registry.setApplicationContext(ctx);

		try {
			final List<ModuleConfig> moduleConfigs = loadModuleConfig(options);

			// load the config properties
			loadConfiguration(moduleConfigs, options);

			// load spring configs
			loadSpringConfiguration(ctx, moduleConfigs, options);

			// start initialization and load init config into config holder
			// setupInitClass(ctx, options.getInitClass(), configHolder);

			ctx.registerShutdownHook();
			ctx.refresh();

			ctx.start();
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			// MiscUtil.closeQuietly(ctx);
		}

		System.out.println("");
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
	 * Load the configuration properties. First the {@link ModuleInit}'s
	 * properties file (set in the {@link ModuleConfig} annotation) will be
	 * loaded. Then the properties file passed via command line will be loaded,
	 * possibly overriding module config properties.
	 * 
	 * @param moduleConfig2
	 * 
	 * @param options
	 * @return
	 * @throws IOException
	 */
	protected static void loadConfiguration(final List<ModuleConfig> moduleConfigs, final BootstrapOptions options)
			throws IOException {

		final ConfigurationHolder configHolder = new ConfigurationHolder();

		// load module's config properties
		for (final ModuleConfig c : moduleConfigs) {
			if (StringUtils.isNotBlank(c.appConfigFile())) {
				final Properties prop = PropertiesUtil.loadPropertiesFromClasspath(c.appConfigFile());

				if (prop != null) {
					configHolder.addConfigruation(prop);
				}
			}
		}

		// load application config, possibly override module configs
		if (StringUtils.isNotBlank(options.getAppConfigFile())) {
			final Properties prop = PropertiesUtil.loadPropertiesFromFile(options.getAppConfigFile());

			if (prop != null) {
				configHolder.addConfigruation(prop);
			}
		}

		Registry.setAppConfiguration(configHolder);
	}

	/**
	 * Inject a bean definition using a {@link BeanDefinitionReader}. This is
	 * necessary, so that the spring context of this module can be merged with
	 * the parent context.
	 * 
	 * @param parentContext
	 */
	protected static void loadSpringConfiguration(final BeanDefinitionRegistry context,
			final List<ModuleConfig> moduleConfigs, final BootstrapOptions options) {

		final BeanDefinitionReader reader = new XmlBeanDefinitionReader(context);

		// load module's spring config
		for (final ModuleConfig c : moduleConfigs) {
			if (StringUtils.isNotBlank(c.springConfigFile())) {
				reader.loadBeanDefinitions(c.springConfigFile());
			}

			SpringUtil.registerBean(context, ModuleDefinition.class, c.moduleName(), null, BeanScope.singleton,
					Arrays.asList(c.moduleName(), c.modelPackagePaths()), false);
		}

		// get application spring config
		if (StringUtils.isNotBlank(options.getSpringConfigFile())) {
			reader.loadBeanDefinitions(options.getSpringConfigFile());
		}

		// if another module init class is registered, we override coreInit.
		if (options.getInitClass() != null) {
			SpringUtil.registerBean(context, options.getInitClass(), null, "coreInit", BeanScope.singleton, null,
					false);
		}
	}

	/**
	 * Get command line arguments, eg. for getting the custom *.properties file.
	 * 
	 * @param args
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	protected static BootstrapOptions parseCommandLine(final String... args) throws ParseException {
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
		}

		ret.setInitClass(initClass);

		return ret;
	}

	/**
	 * Sets org.reflections logging to warnings, as we scan all package paths.
	 * This causes a lot of debug messages being logged.
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
}
