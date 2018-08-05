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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import io.spotnext.core.support.util.FileUtils;
import io.spotnext.instrumentation.transformer.AbstractBaseClassTransformer;
import io.spotnext.maven.Constants;
import io.spotnext.maven.util.JarTransformer;
import ch.qos.logback.core.util.CloseUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 * @author Marco Semiao
 */
@SuppressFBWarnings("REC_CATCH_EXCEPTION")
@Mojo(name = "transform-types", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true)
public class TransformTypesMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	@Parameter(property = "classFileTransformers", name = "classFileTransformers", alias = "transformers", required = true)
	private List<String> classFileTransformers;

	@Parameter
	private boolean includeJars;

	@Override
	@SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_BAD_PRACTICE")
	public void execute() throws MojoExecutionException, MojoFailureException {
		final ClassLoader cl = getClassloader();
		final List<ClassFileTransformer> transformers = getClassFileTransformers(cl);

		if (CollectionUtils.isNotEmpty(transformers)) {
			for (final File f : FileUtils.getFiles(project.getBuild().getOutputDirectory())) {
				if (f.getName().endsWith(Constants.CLASS_EXTENSION)) {
					String relativeClassFilePath = StringUtils.remove(f.getPath(),
							project.getBuild().getOutputDirectory());
					relativeClassFilePath = StringUtils.removeStart(relativeClassFilePath, "/");
					final String className = relativeClassFilePath.substring(0,
							relativeClassFilePath.length() - Constants.CLASS_EXTENSION.length());

					byte[] byteCode;
					try {
						byteCode = Files.readAllBytes(f.toPath());
					} catch (final IOException e) {
						throw new MojoExecutionException(String.format("Can't read bytecode for class %s", className),
								e);
					}

					byte[] modifiedByteCode = byteCode;

					for (final ClassFileTransformer t : transformers) {
						try {
							modifiedByteCode = t.transform(cl, className, null, null, modifiedByteCode);
						} catch (final IllegalClassFormatException e) {
							getLog().warn(String.format("Can't transform class %s, transformer %s", className,
									t.getClass().getSimpleName()));
						}
					}

					if (modifiedByteCode != null && modifiedByteCode.length > 0 && modifiedByteCode != byteCode) {
						OutputStream fileWriter = null;

						try {
							fileWriter = new FileOutputStream(f);
							fileWriter.write(modifiedByteCode);
						} catch (final IOException e) {
							throw new MojoExecutionException(
									"Could not write modified class: " + relativeClassFilePath);
						} finally {
							CloseUtil.closeQuietly(fileWriter);
							getLog().info("Applied transformation to type: " + f.getAbsolutePath());
						}
					} else {
						getLog().debug("No transformation was applied to type: " + f.getAbsolutePath());
					}

				}
			}

			if (includeJars) {
				final String packaging = project.getPackaging();
				final Artifact artifact = project.getArtifact();

				try {
					if ("jar".equals(packaging) && artifact != null) {
						final File source = artifact.getFile();

						if (source.isFile()) {
							final File destination = new File(source.getParent(), "instrument.jar");

							final JarTransformer transform = new JarTransformer(getLog(), cl, Arrays.asList(source),
									transformers);
							transform.transform(destination);

							final File sourceRename = new File(source.getParent(), "notransform-" + source.getName());

							source.renameTo(sourceRename);
							destination.renameTo(source);
						}
					} else {
						getLog().debug("Not a jar file");
					}
				} catch (final Exception e) {
					throw new MojoExecutionException(e.getMessage(), e);
				}
			}
		}
	}

	private ClassLoader getClassloader() throws MojoExecutionException {
		try {
			final List<String> compileClasspathElements = project.getCompileClasspathElements();
			final List<URL> classPathUrls = new ArrayList<URL>();

			for (final String path : compileClasspathElements) {
				classPathUrls.add(new File(path).toURI().toURL());
			}

			final URLClassLoader urlClassLoader = URLClassLoader.newInstance(
					classPathUrls.toArray(new URL[classPathUrls.size()]),
					Thread.currentThread().getContextClassLoader());

			return urlClassLoader;
		} catch (final Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	private List<ClassFileTransformer> getClassFileTransformers(final ClassLoader cl) throws MojoExecutionException {
		try {
			final List<String> compileClasspathElements = project.getCompileClasspathElements();
			final List<String> classPathUrls = new ArrayList<>();

			for (final String path : compileClasspathElements) {
				classPathUrls.add(new File(path).toURI().toURL().getFile());
			}

			if (CollectionUtils.isNotEmpty(classFileTransformers)) {
				final List<ClassFileTransformer> list = new ArrayList<>(classFileTransformers.size());

				for (final String classFileTransformer : classFileTransformers) {
					final Class<?> clazz = cl.loadClass(classFileTransformer);
					final ClassFileTransformer transformer = (ClassFileTransformer) clazz.newInstance();

					if (transformer instanceof AbstractBaseClassTransformer) {
						final AbstractBaseClassTransformer baseClassTransformer = ((AbstractBaseClassTransformer) transformer);

						baseClassTransformer.addClassPaths(project.getBuild().getOutputDirectory());
						baseClassTransformer.addClassPaths(classPathUrls);
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
}
