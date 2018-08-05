package io.spotnext.core.infrastructure.support.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BuildInfo {
	@Value("${git.branch}")
	private String branch;
	@Value("${git.build.host}")
	private String buildHost;
	@Value("${git.build.time}")
	private String buildTime;
	@Value("${git.build.user.email}")
	private String buildUserEmail;
	@Value("${git.build.user.name}")
	private String buildUserName;
	@Value("${git.build.version}")
	private String buildVersion;
	@Value("${git.closest.tag.commit.count}")
	private String closestTagCommitCount;
	@Value("${git.closest.tag.name}")
	private String closestTagName;
	@Value("${git.commit.id}")
	private String commitId;
	@Value("${git.commit.id.abbrev}")
	private String commitIdAbbrev;
	@Value("${git.commit.id.describe}")
	private String commitIdDescribe;
	@Value("${git.commit.id.describe-short}")
	private String commitIdDescribeShort;
	@Value("${git.commit.message.full}")
	private String commitMessageFull;
	@Value("${git.commit.message.short}")
	private String commitMessageShort;
	@Value("${git.commit.time}")
	private String commitTime;
	@Value("${git.commit.user.email}")
	private String commitUserEmail;
	@Value("${git.commit.user.name}")
	private String commitUserName;
	@Value("${git.dirty}")
	private String dirty;
	@Value("${git.remote.origin.url}")
	private String remoteOriginUrl;
	@Value("${git.tags}")
	private String tags;

	public String getBranch() {
		return branch;
	}

	public String getBuildHost() {
		return buildHost;
	}

	public String getBuildTime() {
		return buildTime;
	}

	public String getBuildUserEmail() {
		return buildUserEmail;
	}

	public String getBuildUserName() {
		return buildUserName;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public String getClosestTagCommitCount() {
		return closestTagCommitCount;
	}

	public String getClosestTagName() {
		return closestTagName;
	}

	public String getCommitId() {
		return commitId;
	}

	public String getCommitIdAbbrev() {
		return commitIdAbbrev;
	}

	public String getCommitIdDescribe() {
		return commitIdDescribe;
	}

	public String getCommitIdDescribeShort() {
		return commitIdDescribeShort;
	}

	public String getCommitMessageFull() {
		return commitMessageFull;
	}

	public String getCommitMessageShort() {
		return commitMessageShort;
	}

	public String getCommitTime() {
		return commitTime;
	}

	public String getCommitUserEmail() {
		return commitUserEmail;
	}

	public String getCommitUserName() {
		return commitUserName;
	}

	public String getDirty() {
		return dirty;
	}

	public String getRemoteOriginUrl() {
		return remoteOriginUrl;
	}

	public String getTags() {
		return tags;
	}

}
