package io.spotnext.core.infrastructure.support.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * <p>BuildInfo class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class BuildInfo {
	@Value("${git.branch?:}")
	private String branch;
	@Value("${git.build.host?:}")
	private String buildHost;
	@Value("${git.build.time?:}")
	private String buildTime;
	@Value("${git.build.user.email?:}")
	private String buildUserEmail;
	@Value("${git.build.user.name?:}")
	private String buildUserName;
	@Value("${git.build.version?:}")
	private String buildVersion;
	@Value("${git.closest.tag.commit.count?:}")
	private String closestTagCommitCount;
	@Value("${git.closest.tag.name?:}")
	private String closestTagName;
	@Value("${git.commit.id?:}")
	private String commitId;
	@Value("${git.commit.id.abbrev?:}")
	private String commitIdAbbrev;
	@Value("${git.commit.id.describe?:}")
	private String commitIdDescribe;
	@Value("${git.commit.id.describe-short?:}")
	private String commitIdDescribeShort;
	@Value("${git.commit.message.full?:}")
	private String commitMessageFull;
	@Value("${git.commit.message.short?:}")
	private String commitMessageShort;
	@Value("${git.commit.time?:}")
	private String commitTime;
	@Value("${git.commit.user.email?:}")
	private String commitUserEmail;
	@Value("${git.commit.user.name?:}")
	private String commitUserName;
	@Value("${git.dirty?:}")
	private String dirty;
	@Value("${git.remote.origin.url?:}")
	private String remoteOriginUrl;
	@Value("${git.tags?:}")
	private String tags;

	/**
	 * <p>Getter for the field <code>branch</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getBranch() {
		return branch;
	}

	/**
	 * <p>Getter for the field <code>buildHost</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getBuildHost() {
		return buildHost;
	}

	/**
	 * <p>Getter for the field <code>buildTime</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getBuildTime() {
		return buildTime;
	}

	/**
	 * <p>Getter for the field <code>buildUserEmail</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getBuildUserEmail() {
		return buildUserEmail;
	}

	/**
	 * <p>Getter for the field <code>buildUserName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getBuildUserName() {
		return buildUserName;
	}

	/**
	 * <p>Getter for the field <code>buildVersion</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getBuildVersion() {
		return buildVersion;
	}

	/**
	 * <p>Getter for the field <code>closestTagCommitCount</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getClosestTagCommitCount() {
		return closestTagCommitCount;
	}

	/**
	 * <p>Getter for the field <code>closestTagName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getClosestTagName() {
		return closestTagName;
	}

	/**
	 * <p>Getter for the field <code>commitId</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCommitId() {
		return commitId;
	}

	/**
	 * <p>Getter for the field <code>commitIdAbbrev</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCommitIdAbbrev() {
		return commitIdAbbrev;
	}

	/**
	 * <p>Getter for the field <code>commitIdDescribe</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCommitIdDescribe() {
		return commitIdDescribe;
	}

	/**
	 * <p>Getter for the field <code>commitIdDescribeShort</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCommitIdDescribeShort() {
		return commitIdDescribeShort;
	}

	/**
	 * <p>Getter for the field <code>commitMessageFull</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCommitMessageFull() {
		return commitMessageFull;
	}

	/**
	 * <p>Getter for the field <code>commitMessageShort</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCommitMessageShort() {
		return commitMessageShort;
	}

	/**
	 * <p>Getter for the field <code>commitTime</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCommitTime() {
		return commitTime;
	}

	/**
	 * <p>Getter for the field <code>commitUserEmail</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCommitUserEmail() {
		return commitUserEmail;
	}

	/**
	 * <p>Getter for the field <code>commitUserName</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCommitUserName() {
		return commitUserName;
	}

	/**
	 * <p>Getter for the field <code>dirty</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDirty() {
		return dirty;
	}

	/**
	 * <p>Getter for the field <code>remoteOriginUrl</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getRemoteOriginUrl() {
		return remoteOriginUrl;
	}

	/**
	 * <p>Getter for the field <code>tags</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTags() {
		return tags;
	}

}
