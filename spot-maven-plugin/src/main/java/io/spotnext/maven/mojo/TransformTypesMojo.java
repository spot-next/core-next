/*
 * Copyright 2015 Marco Semiao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.spotnext.maven.mojo;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.sonatype.plexus.build.incremental.BuildContext;

import ch.qos.logback.core.util.CloseUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.instrumentation.transformer.AbstractBaseClassTransformer;
import io.spotnext.maven.Constants;
import io.spotnext.maven.util.JarTransformer;
import io.spotnext.support.util.FileUtils;

/**
 * <p>
 * TransformTypesMojo class.
 * </p>
 *
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 * @author Marco Semiao
 * @since 1.0
 */
@SuppressFBWarnings("REC_CATCH_EXCEPTION")
@Mojo(name = "transform-types", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class TransformTypesMojo extends AbstractMojo {

	@Component
	protected BuildContext buildContext;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	@Parameter(property = "classFileTransformers", required = true)
	private List<String> classFileTransformers;

	@Parameter(property = "debug", required = false)
	private boolean debug = false;

	@Parameter(property = "skip", required = false)
	private boolean skip = false;

	@Parameter
	private boolean includeJars;

	/** {@inheritDoc} */
	@Override
	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
	public void execute() throws MojoExecutionException {
		if (skip) {
			getLog().info("Skipping type transformation!");
			return;
		}

		trackExecution("start");

		final ClassLoader classLoader = getClassloader();
		final List<ClassFileTransformer> transformers = getClassFileTransformers(classLoader);

//		final ExecutorService executorService = Executors.newFixedThreadPool(4);

		List<File> classFiles = FileUtils.getFiles(project.getBuild().getOutputDirectory(), f -> f.getAbsolutePath().endsWith(".class"));
		getLog().debug("Found class files for processing: " + classFiles.stream().map(f -> f.getName()).collect(Collectors.joining(", ")));

		if (CollectionUtils.isNotEmpty(transformers)) {
			for (final File f : classFiles) {
				if (f.getName().endsWith(Constants.CLASS_EXTENSION)) {
//					executorService.submit(() -> {
					String relativeClassFilePath = StringUtils.remove(f.getPath(),
							project.getBuild().getOutputDirectory());
					relativeClassFilePath = StringUtils.removeStart(relativeClassFilePath, "/");
					final String className = relativeClassFilePath.substring(0,
							relativeClassFilePath.length() - Constants.CLASS_EXTENSION.length());

					trackExecution("Loading class: " + f.getAbsolutePath());

					byte[] byteCode;
					try {
						byteCode = Files.readAllBytes(f.toPath());
					} catch (final IOException e) {
						String message = String.format("Can't read bytecode for class %s", className);
						buildContext.addMessage(f, 0, 0, message, BuildContext.SEVERITY_ERROR, e);
						throw new IllegalStateException(message, e);
					}

					trackExecution("Loaded class: " + f.getAbsolutePath());

					for (final ClassFileTransformer t : transformers) {
						try {

							// log exceptions into separate folder, to be able to inspect them even if Eclipse swallows them ...
							if (t instanceof AbstractBaseClassTransformer) {
								((AbstractBaseClassTransformer) t).setErrorLogger(this::logError);
							}

							// returns null if nothing has been transformed
							byteCode = t.transform(classLoader, className, null, null, byteCode);
						} catch (final Exception e) {
							trackExecution("Exception during transformation of class: " + f.getAbsolutePath() + "\n" + e.getMessage());
							String message = String.format("Can't transform class %s, transformer %s: %s", className,
									t.getClass().getSimpleName(), ExceptionUtils.getStackTrace(e));
							buildContext.addMessage(f, 0, 0, message, BuildContext.SEVERITY_ERROR, e);
						}
					}

					if (byteCode != null && byteCode.length > 0) {
						try {
							Files.write(f.toPath(), byteCode, StandardOpenOption.CREATE, StandardOpenOption.WRITE,
									StandardOpenOption.TRUNCATE_EXISTING);

							trackExecution("Saved transformed class: " + f.getAbsolutePath());
						} catch (final IOException e) {
							String message = "Could not write modified class: " + relativeClassFilePath;
							buildContext.addMessage(f, 0, 0, message, BuildContext.SEVERITY_ERROR, e);
							throw new IllegalStateException(message);
						} finally {
							buildContext.refresh(f);
							getLog().info("Applied transformation to type: " + f.getAbsolutePath());
						}
					} else {
						trackExecution("No changes made for class: " + f.getAbsolutePath());
						getLog().debug("No transformation was applied to type: " + f.getAbsolutePath());
					}
//					});
				}
			}

			trackExecution("All classes in build output folder transformed");

//			executorService.shutdown();
//
//			try {
//				executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
//			} catch (InterruptedException e) {
//				throw new MojoExecutionException("Could not execute type transformation", e);
//			}

			if (includeJars) {
				final String packaging = project.getPackaging();
				final Artifact artifact = project.getArtifact();

				if ("jar".equals(packaging) && artifact != null) {
					try {
						final File source = artifact.getFile();

						if (source.isFile()) {
							final File destination = new File(source.getParent(), "instrument.jar");

							final JarTransformer transformer = new JarTransformer(getLog(), classLoader, Arrays.asList(source),
									transformers);
							transformer.transform(destination);

							final File sourceRename = new File(source.getParent(), "notransform-" + source.getName());

							source.renameTo(sourceRename);
							destination.renameTo(sourceRename);

							buildContext.refresh(destination);
						}
					} catch (final Exception e) {
						buildContext.addMessage(artifact.getFile(), 0, 0, e.getMessage(), BuildContext.SEVERITY_ERROR, e);
						throw new MojoExecutionException(e.getMessage(), e);
					}
				} else {
					getLog().debug("Not a jar file");
				}
			}
		}
	}

	@SuppressFBWarnings(value = "DM_DEFAULT_ENCODING", justification = "false positive")
	protected void logError(Throwable cause) {
		final String tempFolder = System.getProperty("java.io.tmpdir");
		final Path logFilePath = Paths.get(tempFolder, "spot-transform.types.log");
		final File logFile = logFilePath.toFile();

		if (logFile.canWrite()) {
			FileWriter writer = null;
			try {
				writer = new FileWriter(logFile);
				writer.write(cause.getMessage());
				writer.write(ExceptionUtils.getStackTrace(cause));
			} catch (Exception e) {
				getLog().error("Can't log to separete error log file " + logFilePath.toString());
			} finally {
				CloseUtil.closeQuietly(writer);
			}
		}
	}

	private ClassLoader getClassloader() throws MojoExecutionException {
		try {
			final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

			final List<URL> classPathUrls = getClasspath();

			final URLClassLoader urlClassLoader = URLClassLoader.newInstance(
					classPathUrls.toArray(new URL[classPathUrls.size()]),
					contextClassLoader);

			trackExecution("Classpath: " + classPathUrls.stream().map(u -> u.toString()).collect(Collectors.joining(",")));

			Thread.currentThread().setContextClassLoader(urlClassLoader);

			return urlClassLoader;
		} catch (final Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private List<URL> getClasspath() throws MalformedURLException, DependencyResolutionRequiredException {
		final List<URL> classPathUrls = new ArrayList<URL>();

		final List<String> classpathElements = project.getRuntimeClasspathElements();

		// add build output folder to classpath
		classpathElements.add(computeDir(project.getBuild().getOutputDirectory()));

		for (final String path : classpathElements) {
			classPathUrls.add(new File(path).toURI().toURL());
		}

		return classPathUrls;
	}

	private String computeDir(String dir) {
		return new File(dir).getAbsolutePath();
	}

	private List<ClassFileTransformer> getClassFileTransformers(final ClassLoader cl) throws MojoExecutionException {
		try {
			final List<URL> classPathUrls = getClasspath();

			if (CollectionUtils.isNotEmpty(classFileTransformers)) {
				final List<ClassFileTransformer> list = new ArrayList<>(classFileTransformers.size());

				for (final String classFileTransformer : classFileTransformers) {
					final Class<?> clazz = cl.loadClass(classFileTransformer);
					final ClassFileTransformer transformer = (ClassFileTransformer) clazz.newInstance();

					if (transformer instanceof AbstractBaseClassTransformer) {
						final AbstractBaseClassTransformer baseClassTransformer = ((AbstractBaseClassTransformer) transformer);

						baseClassTransformer.addClassPaths(project.getBuild().getOutputDirectory());
						baseClassTransformer.addClassPaths(classPathUrls.stream().map(u -> u.getFile()).collect(Collectors.toList()));
					}

					list.add(transformer);
				}

				return list;
			} else {
				getLog().warn("No class file transformers configured!");
				return Collections.emptyList();
			}
		} catch (final Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private void trackExecution(String message) throws IllegalStateException {
		if (debug) {
			try {
				File tempDir = FileUtils.getTempDirectory();

				Files.write(Paths.get(tempDir.getAbsolutePath(), "transform-classes.log"),
						(new Date().toString() + ": " + message + "\n").getBytes(StandardCharsets.UTF_8),
						StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			} catch (Exception e) {
				throw new IllegalStateException("error", e);
			}
		}
	}
}
