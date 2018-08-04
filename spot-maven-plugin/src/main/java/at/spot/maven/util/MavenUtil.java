package at.spot.maven.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;

import at.spot.core.support.util.ValidationUtil;

public class MavenUtil {

	/**
	 * Resolves a artifact dependency file.
	 * 
	 * @param repository
	 * @param artifact
	 * @throws IOException
	 */
	public static File getArtiactFile(final ArtifactRepository repository, final Artifact artifact) throws IOException {
		ValidationUtil.validateNotNull("Repository must be set", repository);
		ValidationUtil.validateNotNull("Artifact must be set", artifact);

		File file = artifact.getFile();

		if (file == null || !file.exists()) {
			final File localRepo = new File(repository.getBasedir());

			if (localRepo.exists()) {
				String[] groupPath = artifact.getGroupId().split("\\.");
				groupPath = ArrayUtils.add(groupPath, artifact.getArtifactId());
				groupPath = ArrayUtils.add(groupPath, artifact.getVersion());
				groupPath = ArrayUtils.add(groupPath,
						artifact.getArtifactId() + "-" + artifact.getVersion() + "." + artifact.getType());

				final Path artifactPath = Paths.get(localRepo.getAbsolutePath(), groupPath);

				file = artifactPath.toFile();
			} else {
				throw new IOException(
						String.format("Repository path not does not exist: %s", localRepo.getAbsolutePath()));
			}

			if (file == null) {
				throw new IOException(String.format("Could not resolve dependency %s", artifact));
			} else if (!file.exists()) {
				throw new IOException(String.format("Artifact file not found: %s", file.getAbsolutePath()));
			}
		}

		return file;
	}
}
