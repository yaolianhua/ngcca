package io.hotcloud.server.buildpack.service;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.module.buildpack.GitCloned;
import io.hotcloud.module.buildpack.GitClonedService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class GitClonedCollectionQuery {

    private final GitClonedService gitClonedService;

    public GitClonedCollectionQuery(GitClonedService gitClonedService) {
        this.gitClonedService = gitClonedService;
    }


    /**
     * Paging query {@link GitCloned} with giving parameter
     *
     * @param user     user's username
     * @param success  whether is success
     * @param pageable {@link Pageable}
     * @return {@link PageResult}
     */
    public PageResult<GitCloned> pagingQuery(String user, Boolean success, Pageable pageable) {
        List<GitCloned> cloneds;
        if (StringUtils.hasText(user)) {
            cloneds = gitClonedService.findAll(user);
        } else {
            cloneds = gitClonedService.findAll();
        }

        List<GitCloned> filtered = filter(cloneds, success);
        return PageResult.ofCollectionPage(filtered, pageable);
    }

    public List<GitCloned> filter(List<GitCloned> cloneds, Boolean success) {
        if (success == null) {
            return cloneds;
        }
        return cloneds.stream()
                .filter(e -> Objects.equals(e.isSuccess(), success))
                .collect(Collectors.toList());
    }
}
