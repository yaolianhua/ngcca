package io.hotcloud.vendor.gitapi.gitlab;

import io.hotcloud.common.model.exception.PlatformException;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Project;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GitlabService {

    private final GitLabApiFactory gitLabApiFactory;

    public GitlabService(GitLabApiFactory gitLabApiFactory) {
        this.gitLabApiFactory = gitLabApiFactory;
    }

    /**
     * <pre>{@code {
     *      "allowMergeOnSkippedPipeline": null,
     *      "approvalsBeforeMerge": null,
     *      "archived": false,
     *      "autoDevopsDeployStrategy": null,
     *      "autoDevopsEnabled": null,
     *      "autocloseReferencedIssues": null,
     *      "avatarUrl": null,
     *      "buildCoverageRegex": null,
     *      "buildGitStrategy": null,
     *      "canCreateMergeRequestIn": null,
     *      "ciConfigPath": null,
     *      "ciDefaultGitDepth": null,
     *      "ciForwardDeploymentEnabled": null,
     *      "containerRegistryEnabled": true,
     *      "createdAt": "2023-02-23T08:54:40.845+00:00",
     *      "creatorId": 23,
     *      "customAttributes": null,
     *      "defaultBranch": "master",
     *      "description": "",
     *      "emailsDisabled": null,
     *      "emptyRepo": null,
     *      "forkedFromProject": null,
     *      "forksCount": 0,
     *      "httpUrlToRepo": "http://10.10.14.80/yaolianhua/simple-java.git",
     *      "id": 269,
     *      "importStatus": "finished",
     *      "initializeWithReadme": null,
     *      "issuesEnabled": true,
     *      "jobsEnabled": true,
     *      "lastActivityAt": "2023-07-20T10:11:12.236+00:00",
     *      "lfsEnabled": true,
     *      "license": null,
     *      "licenseUrl": null,
     *      "markedForDeletionOn": null,
     *      "mergeMethod": "merge",
     *      "mergeRequestsEnabled": true,
     *      "name": "simple-java",
     *      "nameWithNamespace": "yaolianhua / simple-java",
     *      "namespace": {
     *           "avatarUrl": null,
     *           "fullPath": "yaolianhua",
     *           "id": 27,
     *           "kind": "user",
     *           "name": "yaolianhua",
     *           "path": "yaolianhua",
     *           "webUrl": null
     *      },
     *      "onlyAllowMergeIfAllDiscussionsAreResolved": false,
     *      "onlyAllowMergeIfPipelineSucceeds": false,
     *      "openIssuesCount": 0,
     *      "owner": {
     *           "avatarUrl": "http://10.10.14.80/uploads/-/system/user/avatar/23/avatar.png",
     *           "createdAt": null,
     *           "email": null,
     *           "id": 23,
     *           "name": "yaolianhua",
     *           "state": "active",
     *           "username": "yaolianhua",
     *           "webUrl": "http://10.10.14.80/yaolianhua"
     *      },
     *      "packagesEnabled": null,
     *      "path": "simple-java",
     *      "pathWithNamespace": "yaolianhua/simple-java",
     *      "permissions": {
     *           "groupAccess": null,
     *           "projectAccess": {
     *                "accessLevel": 40,
     *                "notificationLevel": 3
     *           }
     *      },
     *      "printingMergeRequestLinkEnabled": true,
     *      "public": null,
     *      "publicJobs": true,
     *      "readmeUrl": "http://10.10.14.80/yaolianhua/simple-java/blob/master/README.md",
     *      "removeSourceBranchAfterMerge": null,
     *      "repositoryStorage": null,
     *      "requestAccessEnabled": false,
     *      "resolveOutdatedDiffDiscussions": false,
     *      "runnersToken": null,
     *      "sharedRunnersEnabled": true,
     *      "sharedWithGroups": [],
     *      "snippetsEnabled": true,
     *      "squashOption": null,
     *      "sshUrlToRepo": "git@10.10.14.80:yaolianhua/simple-java.git",
     *      "starCount": 0,
     *      "statistics": null,
     *      "suggestionCommitMessage": null,
     *      "tagList": [],
     *      "visibility": "public",
     *      "visibilityLevel": null,
     *      "wallEnabled": null,
     *      "webUrl": "http://10.10.14.80/yaolianhua/simple-java",
     *      "wikiEnabled": true
     * }}</pre>
     */
    public List<Project> listProjects(GitLabRequestParameter parameter) {
        GitLabApi gitLabApi;
        if (parameter.isBasicAuth()) {
            gitLabApi = gitLabApiFactory.create(parameter.getHost(), parameter.getUsername(), parameter.getPassword());
        } else {
            gitLabApi = gitLabApiFactory.create(parameter.getHost(), parameter.getAccessToken());
        }

        try {
            return gitLabApi.getProjectApi().getProjects();
        } catch (GitLabApiException e) {
            throw new PlatformException("Get gitlab projects error：" + e.getMessage(), 500);
        } finally {
            gitLabApi.close();
        }
    }

    public List<Project> listOwnedProjects(GitLabRequestParameter parameter) {
        GitLabApi gitLabApi;
        if (parameter.isBasicAuth()) {
            gitLabApi = gitLabApiFactory.create(parameter.getHost(), parameter.getUsername(), parameter.getPassword());
        } else {
            gitLabApi = gitLabApiFactory.create(parameter.getHost(), parameter.getAccessToken());
        }
        try {
            return gitLabApi.getProjectApi().getOwnedProjects();
        } catch (GitLabApiException e) {
            throw new PlatformException("Get gitlab projects error：" + e.getMessage(), 500);
        } finally {
            gitLabApi.close();
        }
    }

    /**
     * <pre>{@code {
     *      "canPush": true,
     *      "commit": {
     *           "author": null,
     *           "authorEmail": "yaolianhua@cloudtogo.cn",
     *           "authorName": "yaolianhua",
     *           "authoredDate": "2023-07-20T10:11:09.000+00:00",
     *           "committedDate": "2023-07-20T10:11:09.000+00:00",
     *           "committerEmail": "yaolianhua@cloudtogo.cn",
     *           "committerName": "yaolianhua",
     *           "createdAt": "2023-07-20T10:11:09.000+00:00",
     *           "id": "fe095292c86268bc8c2ae0c67665b31a7cf19898",
     *           "lastPipeline": null,
     *           "message": "hook",
     *           "parentIds": null,
     *           "projectId": null,
     *           "shortId": "fe095292",
     *           "stats": null,
     *           "status": null,
     *           "timestamp": null,
     *           "title": "hook",
     *           "url": null,
     *           "webUrl": null
     *      },
     *      "default": false,
     *      "developersCanMerge": false,
     *      "developersCanPush": false,
     *      "merged": false,
     *      "name": "private-nexus",
     *      "protected": false,
     *      "webUrl": null
     * }}</pre>
     */
    public List<Branch> listBranches(Object projectIdOrPath, GitLabRequestParameter parameter) {
        GitLabApi gitLabApi;
        if (parameter.isBasicAuth()) {
            gitLabApi = gitLabApiFactory.create(parameter.getHost(), parameter.getUsername(), parameter.getPassword());
        } else {
            gitLabApi = gitLabApiFactory.create(parameter.getHost(), parameter.getAccessToken());
        }

        try {
            return gitLabApi.getRepositoryApi().getBranches(projectIdOrPath);
        } catch (GitLabApiException e) {
            throw new PlatformException("Get gitlab branches error：" + e.getMessage(), 500);
        } finally {
            gitLabApi.close();
        }
    }
}
