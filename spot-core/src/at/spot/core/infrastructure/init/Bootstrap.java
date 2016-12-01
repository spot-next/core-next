package at.spot.core.infrastructure.init;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.access.BootstrapException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.GenericApplicationContext;

import at.spot.core.CoreInit;
import at.spot.core.infrastructure.spring.support.Registry;
import at.spot.core.support.util.SpringUtil;

/**
 * This is the main entry point to startup a spOt instance. First the classpath
 * is scanned for {@link ModuleInit} implementations (there should be one for
 * each spot module) which it then tries to load. The init classes take care of
 * all necessary initialization for their module.
 */
public class Bootstrap {
	public static final long MAIN_THREAD_ID = Thread.currentThread().getId();

	public static void main(final String[] args) throws Exception {
		setDefaultLocale();
		setLogSettings();

		// create a generic spring context
		final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

		// inject spring context into Registry
		Registry.setApplicationContext(ctx);

		try {
			// setupConfigurationHolder(ctx);

			final ConfigurationHolder configHolder = new ConfigurationHolder();

			{ // parse command line properties
				final BootstrapOptions opts = parseCommandLine(args);

				// load application config
				if (StringUtils.isNotBlank(opts.getPropertyFile())) {
					loadPropeperties(configHolder, opts.getPropertyFile());
				}

				// start initialization
				setupInitClass(ctx, opts.getInitClass());
			}

			ctx.registerShutdownHook();
			ctx.refresh();

			ctx.start();
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			// MiscUtil.closeQuietly(ctx);
		}
	}

	protected static void setupConfigurationHolder(final GenericApplicationContext context) {
		SpringUtil.registerBean(((BeanDefinitionRegistry) context.getBeanFactory()), ConfigurationHolder.class, null,
				true);
	}

	protected static void setupInitClass(final GenericApplicationContext context,
			final Class<? extends ModuleInit> initClass) {

		SpringUtil.registerBean(((BeanDefinitionRegistry) context.getBeanFactory()), initClass, null, true);
	}

	protected static void setDefaultLocale() {
		LocaleContextHolder.setLocale(Locale.ENGLISH);
	}

	/**
	 * Get command line arguments, eg. for getting the custom *.properties file.
	 * 
	 * @param args
	 * @return
	 * @throws ParseException
	 */
	protected static BootstrapOptions parseCommandLine(final String... args) throws ParseException {
		final BootstrapOptions ret = new BootstrapOptions();

		final Options options = new Options();
		options.addOption(OptionBuilder.withLongOpt("p").withDescription("application the properties file").hasArg()
				.withArgName("properties").create("properties"));

		final CommandLineParser parser = new DefaultParser();
		final CommandLine cmd = parser.parse(options, args);

		if (cmd.hasOption("p")) {
			ret.setPropertyFile(cmd.getOptionValue("p"));
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
	 * Inject custom properties into the spring context.
	 * 
	 * @param springContext
	 * @param propertyFile
	 * @throws IOException
	 */
	protected static void loadPropeperties(final ConfigurationHolder configHolder, final String propertyFile)
			throws IOException {

		// resolve path
		Path propPath = Paths.get(propertyFile);

		if (!propPath.isAbsolute()) {
			final Path currentDir = Paths.get(System.getProperty("user.dir"));
			propPath = currentDir.resolve(propPath);
		}

		configHolder.addConfigruation(propPath.toFile());
	}

	/**
	 * Sets org.reflections logging to warnings, as we scan all package paths.
	 * This causes a lot of debug messages being logged.
	 */
	protected static void setLogSettings() {
		System.setProperty("org.slf4j.simpleLogger.log.org.reflections", "warn");
	}
}
