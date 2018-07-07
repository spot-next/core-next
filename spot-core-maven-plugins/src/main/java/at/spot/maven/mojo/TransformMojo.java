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
package at.spot.maven.mojo;

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

import at.spot.core.support.util.FileUtils;
import at.spot.instrumentation.transformer.AbstractBaseClassTransformer;
import at.spot.maven.Constants;
import ch.qos.logback.core.util.CloseUtil;

/**
 * @see <a href="http://marcosemiao4j.wordpress.com">Marco4J</a>
 *
 * @author Marco Semiao
 */
@Mojo(name = "transform", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE, threadSafe = true)
public class TransformMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	@Parameter
	private String[] classFileTransformers;

	@Parameter
	private boolean includeJars;

	public void execute() throws MojoExecutionException, MojoFailureException {
		final String packaging = project.getPackaging();
		final Artifact artifact = project.getArtifact();

		final ClassLoader cl = getClassloader();
		final List<ClassFileTransformer> transformers = getClassFileTransformers(cl);

		if (CollectionUtils.isNotEmpty(transformers)) {
			for (File f : FileUtils.getFiles(project.getBuild().getOutputDirectory())) {
				if (f.getName().endsWith(Constants.CLASS_EXTENSION)) {
					String relativeClassFilePath = StringUtils.remove(f.getPath(),
							project.getBuild().getOutputDirectory());
					relativeClassFilePath = StringUtils.removeStart(relativeClassFilePath, "/");
					final String className = relativeClassFilePath.substring(0,
							relativeClassFilePath.length() - Constants.CLASS_EXTENSION.length());

					byte[] byteCode;
					try {
						byteCode = Files.readAllBytes(f.toPath());
					} catch (IOException e) {
						throw new MojoExecutionException(String.format("Can't read bytecode for class %s", className),
								e);
					}

					byte[] modifiedByteCode = byteCode;

					for (ClassFileTransformer t : transformers) {
						try {
							modifiedByteCode = t.transform(cl, className, null, null, modifiedByteCode);
						} catch (IllegalClassFormatException e) {
							getLog().warn(String.format("Can't transform class %s, transformer %s", className,
									t.getClass().getSimpleName()));
						}
					}

					if (modifiedByteCode != null && modifiedByteCode != byteCode) {
						getLog().debug("Transformed: " + className);

						OutputStream fileWriter = null;

						try {
							fileWriter = new FileOutputStream(f);
							fileWriter.write(modifiedByteCode);
						} catch (IOException e) {
							throw new MojoExecutionException(
									"Could not write modified class: " + relativeClassFilePath);
						} finally {
							CloseUtil.closeQuietly(fileWriter);
						}
					}

				}
			}

			// if (includeJars) {
			// try {
			// if ("jar".equals(packaging) && artifact != null) {
			// final File source = artifact.getFile();
			// final File destination = new File(source.getParent(), "instrument.jar");
			//
			// final JarTransformer transform = new JarTransformer(getLog(), cl,
			// Arrays.asList(source),
			// transformers);
			// transform.transform(destination);
			//
			// final File sourceRename = new File(source.getParent(), "notransform-" +
			// source.getName());
			//
			// source.renameTo(sourceRename);
			// destination.renameTo(source);
			// } else {
			// getLog().debug("Not a jar file");
			// }
			// } catch (final Exception e) {
			// throw new MojoExecutionException(e.getMessage(), e);
			// }
			// }
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
			final List<URL> classPathUrls = new ArrayList<URL>();

			for (final String path : compileClasspathElements) {
				classPathUrls.add(new File(path).toURI().toURL());
			}

			final List<ClassFileTransformer> list = new ArrayList<ClassFileTransformer>(classFileTransformers.length);
			for (final String classFileTransformer : classFileTransformers) {
				final Class<?> clazz = cl.loadClass(classFileTransformer);
				final ClassFileTransformer transformer = (ClassFileTransformer) clazz.newInstance();

				if (transformer instanceof AbstractBaseClassTransformer) {
					((AbstractBaseClassTransformer) transformer).addClassPaths(project.getBuild().getOutputDirectory());
				}

				list.add(transformer);
			}

			return list;
		} catch (final Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}
}
