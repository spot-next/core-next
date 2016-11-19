package at.spot.core.infrastructure.init;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import at.spot.core.infrastructure.spring.support.Registry;
import at.spot.core.support.util.MiscUtil;

public class Bootstrap {
	public static final long MAIN_THREAD_ID = Thread.currentThread().getId();

	public static void main(final String[] args) throws Exception {
		// find all module init classes
		final Reflections reflections = new Reflections(
				new ConfigurationBuilder().setUrls(ClasspathHelper.forJavaClassPath()));
		final Set<Class<? extends ModuleInit>> inits = reflections.getSubTypesOf(ModuleInit.class);

		// create a generic spring context
		final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

		// inject spring context into Registry
		Registry.setApplicationContext(ctx);

		try {
			{ // register all found module inits in that spring context
				for (final Class<? extends ModuleInit> init : inits) {
					init.newInstance().injectBeanDefinition(ctx);
				}
			}

			{ // register command line properties
				BootstrapOptions opts = parseCommandLine(args);

				if (StringUtils.isNotBlank(opts.getPropertyFile())) {
					loadPropeperties(ctx, opts.getPropertyFile());
				}
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

	/**
	 * Get command line arguments, eg. for getting the custom *.properties file.
	 * 
	 * @param args
	 * @return
	 * @throws ParseException
	 */
	protected static BootstrapOptions parseCommandLine(String... args) throws ParseException {
		BootstrapOptions ret = new BootstrapOptions();

		Options options = new Options();
		options.addOption(OptionBuilder.withLongOpt("p").withDescription("application the properties file").hasArg()
				.withArgName("properties").create("properties"));

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);

		if (cmd.hasOption("p")) {
			ret.setPropertyFile(cmd.getOptionValue("p"));
		}

		return ret;
	}

	/**
	 * Inject custom properties into the spring context.
	 * 
	 * @param springContext
	 * @param propertyFile
	 * @throws IOException
	 */
	protected static void loadPropeperties(GenericApplicationContext springContext, String propertyFile)
			throws IOException {

		// System.out.println("Loading properties: " + opts.getPropertyFile());

		// resolve path
		Path propPath = Paths.get(propertyFile);

		if (!propPath.isAbsolute()) {
			Path currentDir = Paths.get(System.getProperty("user.dir"));
			propPath = currentDir.resolve(propPath);
		}

		InputStream propStream = null;

		try {
			propStream = new FileInputStream(propPath.toFile());

			Properties prop = new Properties();
			prop.load(propStream);

			ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) springContext)
					.getBeanFactory();
			beanFactory.registerSingleton("applicationConfiguration", prop);
		} finally {
			MiscUtil.closeQuietly(propStream);
		}
	}

	/**
	 * Sets org.reflections logging to warnings, as we scan all package paths.
	 * This causes a lot of debug messages being logged.
	 */
	protected static void setLogSettings() {
		System.setProperty("org.slf4j.simpleLogger.log.org.reflections", "warn");
	}
}
