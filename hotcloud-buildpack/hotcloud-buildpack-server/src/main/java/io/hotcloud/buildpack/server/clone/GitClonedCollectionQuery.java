package io.hotcloud.buildpack.server.clone;

import io.hotcloud.buildpack.api.clone.GitCloned;
import io.hotcloud.buildpack.api.clone.GitClonedService;
import io.hotcloud.common.PageResult;
import io.hotcloud.common.Pageable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
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
            cloneds = gitClonedService.listCloned(user);
        } else {
            cloneds = gitClonedService.listAll();
        }

        List<GitCloned> filtered = filter(cloneds, success);
        return PageResult.ofPage(filtered, pageable.getPage(), pageable.getPageSize());
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
