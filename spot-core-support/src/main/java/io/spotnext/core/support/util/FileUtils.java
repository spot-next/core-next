package io.spotnext.core.support.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * <p>
 * FileUtils class.
 * </p>
 *
 * @since 1.0
 * @author mojo2012
 * @version 1.0
 */
@SuppressFBWarnings({ "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", "OS_OPEN_STREAM" })
public class FileUtils {

	/**
	 * list files in the given directory and subdirs (with recursion)
	 *
	 * @param paths a {@link java.lang.String} object.
	 * @return a {@link java.util.List} object.
	 */
	public static List<File> getFiles(final String paths, Predicate<File> filter) {
		final List<File> filesList = new ArrayList<>();
		for (final String path : paths.split(File.pathSeparator)) {
			final File file = new File(path);
			if (file.isDirectory()) {
				recurse(filesList, file);
			} else {
				filesList.add(file);
			}
		}

		return filesList.stream().filter(f -> filter == null || filter.test(f)).collect(Collectors.toList());
	}

	/**
	 * <p>
	 * recurse.
	 * </p>
	 *
	 * @param filesList a {@link java.util.List} object.
	 * @param f         a {@link java.io.File} object.
	 */
	protected static void recurse(final List<File> filesList, final File f) {
		final File[] list = f.listFiles();
		for (final File file : list) {
			if (file.isDirectory()) {
				recurse(filesList, file);
			} else {
				filesList.add(file);
			}
		}
	}

	/**
	 * Reads a file into an {@link java.io.InputStream}.
	 *
	 * @param filePath a {@link java.lang.String} object.
	 * @return a {@link java.io.InputStream} object.
	 * @throws java.io.FileNotFoundException if any.
	 */
	public static InputStream readFile(final String filePath) throws FileNotFoundException {
		return new FileInputStream(filePath);
	}

	/**
	 * Reads a file into an {@link java.io.InputStream}.
	 *
	 * @param file a {@link java.io.File} object.
	 * @return a {@link java.io.InputStream} object.
	 * @throws java.io.FileNotFoundException if any.
	 */
	public static InputStream readFile(final File file) throws FileNotFoundException {
		return readFile(file.getAbsolutePath());
	}

	/**
	 * <p>
	 * readFileFromZipFile.
	 * </p>
	 *
	 * @param zipFile          a {@link java.util.zip.ZipFile} object.
	 * @param relativeFilePath a {@link java.lang.String} object.
	 * @return a {@link java.io.InputStream} object.
	 * @throws java.io.FileNotFoundException if any.
	 */
	public static InputStream readFileFromZipFile(final ZipFile zipFile, final String relativeFilePath)
			throws FileNotFoundException {

		final Enumeration<? extends ZipEntry> e = zipFile.entries();

		while (e.hasMoreElements()) {
			final ZipEntry entry = e.nextElement();
			// if the entry is not directory and matches relative file then
			// extract it
			if (!entry.isDirectory() && entry.getName().equals(relativeFilePath)) {
				try {
					return zipFile.getInputStream(entry);
				} catch (final IOException e1) {
					throw new FileNotFoundException(String.format("Cannot read file '%s' from zip file '%s'.",
							relativeFilePath, zipFile.getName()));
				}
			}
		}

		throw new FileNotFoundException(
				String.format("File '%s' not found in zip file '%s'.", relativeFilePath, zipFile.getName()));
	}

	/**
	 * Reads a file from a zip file and returns an {@link java.io.InputStream} object. If the file is not found, an exception is thrown.
	 *
	 * @param zipFilePath      a {@link java.lang.String} object.
	 * @param relativeFilePath a {@link java.lang.String} object.
	 * @return a {@link java.io.InputStream} object.
	 * @throws java.io.FileNotFoundException if any.
	 */
	public static InputStream readFileFromZipFile(final String zipFilePath, final String relativeFilePath)
			throws FileNotFoundException {

		try {
			return readFileFromZipFile(new ZipFile(zipFilePath), relativeFilePath);
		} catch (final IOException e) {
			throw new FileNotFoundException(
					String.format("Cannot read file '%s' from zip file '%s'.", relativeFilePath, zipFilePath));
		}
	}

	/**
	 * List the content of the given jar
	 *
	 * @param jarPath a {@link java.lang.String} object.
	 * @return a {@link java.util.List} object.
	 * @throws java.io.IOException if any.
	 */
	public static List<String> getFileListFromJar(final String jarPath) throws IOException {
		final List<String> content = new ArrayList<String>();
		final JarFile jarFile = new JarFile(jarPath);
		final Enumeration<JarEntry> e = jarFile.entries();

		while (e.hasMoreElements()) {
			final JarEntry entry = e.nextElement();
			final String name = entry.getName();
			content.add(name);
		}

		MiscUtil.closeQuietly(jarFile);

		return content;
	}

}
