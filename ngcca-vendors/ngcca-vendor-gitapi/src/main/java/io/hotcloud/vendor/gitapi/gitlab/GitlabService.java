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

    private final GitLabApi gitLabApi;

    public GitlabService(GitLabApi gitLabApi) {
        this.gitLabApi = gitLabApi;
    }

    public List<Project> listProjects() {
        try {
            return gitLabApi.getProjectApi().getProjects();
        } catch (GitLabApiException e) {
            throw new PlatformException("Get gitlab projects error：" + e.getMessage(), 500);
        }
    }

    public List<Branch> listBranches(Object projectIdOrPath) {
        try {
            return gitLabApi.getRepositoryApi().getBranches(projectIdOrPath);
        } catch (GitLabApiException e) {
            throw new PlatformException("Get gitlab branches error：" + e.getMessage(), 500);
        }
    }
}
