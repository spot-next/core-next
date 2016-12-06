package at.spot.core.support.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.jboss.logging.Logger;

public class PropertiesUtil {

	private static Logger LOG = Logger.getLogger(PropertiesUtil.class);

	/**
	 * Loads a {@link Properties} from a file.
	 * 
	 * @param propertyFile
	 *            if it is a relative path "user.dir" will be used to resolve
	 *            it.
	 * @return null if file can't be found
	 */
	public static Properties loadPropertiesFromFile(final String propertyFile) {
		Path propPath = Paths.get(propertyFile);

		if (!propPath.isAbsolute()) {
			final Path currentDir = Paths.get(System.getProperty("user.dir"));
			propPath = currentDir.resolve(propPath);
		}

		return loadPropertiesFromFile(propPath.toFile());
	}

	/**
	 * Loads {@link Properties} from the classpath.
	 * 
	 * @param classPathFileName
	 * @return
	 */
	public static Properties loadPropertiesFromClasspath(final String classPathFileName) {
		final InputStream input = PropertiesUtil.class.getClassLoader().getResourceAsStream(classPathFileName);

		final Properties prop = new Properties();

		try {
			prop.load(input);
		} catch (final IOException e) {
			LOG.warn(e.getMessage());
		}

		return prop;
	}

	/**
	 * Loads {@link Properties} from a file.
	 * 
	 * @param file
	 * @return null if file can't be found
	 */
	public static Properties loadPropertiesFromFile(final File file) {
		Properties prop = null;

		try {
			final FileReader reader = new FileReader(file);

			prop = new Properties();
			prop.load(reader);
		} catch (final IOException e) {
			LOG.warn(e.getMessage());
		}

		return prop;
	}
}
