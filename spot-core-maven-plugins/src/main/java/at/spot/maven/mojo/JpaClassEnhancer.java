package at.spot.maven.mojo;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * @description Generates the java source code for the defined item types.
 * @requiresDependencyResolution test
 */
@Mojo(name = "jpaClassEnhancer", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE, requiresProject = true)
public class JpaClassEnhancer extends AbstractMojo {

	@Parameter(property = "basedir", defaultValue = "${project.basedir}", readonly = true, required = true)
	protected String projectBaseDir;

	protected File targetClassesDirectory = null;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Enhancing item types with JPA annotations.");

	}

}
