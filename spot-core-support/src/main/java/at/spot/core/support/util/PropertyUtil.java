package at.spot.core.support.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import org.springframework.util.ResourceUtils;

public class PropertyUtil {

	/**
	 * Loads a {@link Properties} from a file from the classpath. The file path
	 * has to be start with "classpath:".
	 * 
	 * @param fileName
	 * @return null if file can't be found
	 */
	public static Properties loadPropertiesFromClassPath(final String fileName) {
		File propfile = null;

		try {
			propfile = ResourceUtils.getFile(fileName);
		} catch (final FileNotFoundException e) {
			// ignore
		}

		return loadPropertiesFromClassPath(propfile);
	}

	/**
	 * Loads {@link Properties} from a file.
	 * 
	 * @param file
	 * @return null if file can't be found
	 */
	public static Properties loadPropertiesFromClassPath(final File file) {
		final Properties prop = new Properties();

		try {
			prop.load(new FileReader(file));
		} catch (final IOException e) {
			// ignore
		}

		return prop;
	}
}
