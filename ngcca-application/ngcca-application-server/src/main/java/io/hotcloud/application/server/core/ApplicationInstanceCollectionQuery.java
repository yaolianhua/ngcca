package io.hotcloud.application.server.core;

import io.hotcloud.application.api.core.ApplicationInstance;
import io.hotcloud.application.api.core.ApplicationInstanceService;
import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ApplicationInstanceCollectionQuery {

    private final ApplicationInstanceService applicationInstanceService;

    public ApplicationInstanceCollectionQuery(ApplicationInstanceService applicationInstanceService) {
        this.applicationInstanceService = applicationInstanceService;
    }

    /**
     * Paging query all application instance with giving parameter
     *
     * @param user     user's username
     * @param success  whether is success
     * @param delete   whether is deleted
     * @param pageable {@link Pageable}
     * @return paged application instance collection
     */
    public PageResult<ApplicationInstance> pagingQuery(@Nullable String user, @Nullable Boolean success, @Nullable Boolean delete, Pageable pageable) {

        List<ApplicationInstance> applicationInstances;
        if (StringUtils.hasText(user)) {
            applicationInstances = applicationInstanceService.findAll(user);
        } else {
            applicationInstances = applicationInstanceService.findAll();
        }

        List<ApplicationInstance> filtered = filter(applicationInstances, success, delete);
        return PageResult.ofCollectionPage(filtered, pageable);

    }

    public List<ApplicationInstance> filter(List<ApplicationInstance> applicationInstances, Boolean success, Boolean deleted) {
        if (success == null && deleted == null) {
            return applicationInstances;
        } else if (success != null && deleted != null) {
            return applicationInstances.stream()
                    .filter(e -> Objects.equals(e.isSuccess(), success) &&
                            Objects.equals(e.isDeleted(), deleted))
                    .collect(Collectors.toList());
        } else if (success == null) {
            return applicationInstances.stream()
                    .filter(e -> Objects.equals(e.isDeleted(), deleted))
                    .collect(Collectors.toList());
        }

        return applicationInstances.stream()
                .filter(e -> Objects.equals(e.isSuccess(), success))
                .collect(Collectors.toList());
    }

}
