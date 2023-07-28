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
