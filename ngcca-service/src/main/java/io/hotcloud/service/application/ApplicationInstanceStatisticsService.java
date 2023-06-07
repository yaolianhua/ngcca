package io.hotcloud.service.application;

import io.hotcloud.module.application.ApplicationInstance;
import io.hotcloud.module.application.ApplicationInstanceService;
import io.hotcloud.module.application.ApplicationInstanceStatistics;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static io.hotcloud.common.model.CommonConstant.SUCCESS_MESSAGE;

@Service
public class ApplicationInstanceStatisticsService {

    private final ApplicationInstanceService applicationInstanceService;

    public ApplicationInstanceStatisticsService(ApplicationInstanceService applicationInstanceService) {
        this.applicationInstanceService = applicationInstanceService;
    }

    /**
     * Get ApplicationInstanceStatistics
     *
     * @param user user's username
     * @return {@link ApplicationInstanceStatistics}
     */
    public ApplicationInstanceStatistics statistics(@Nullable String user) {
        boolean hasUser = StringUtils.hasText(user);

        if (hasUser) {
            List<ApplicationInstance> instances = applicationInstanceService.findAll(user);
            return statistics(instances);
        }


        List<ApplicationInstance> instances = applicationInstanceService.findAll();
        return statistics(instances);
    }

    public ApplicationInstanceStatistics statistics(List<ApplicationInstance> instances) {

        int deleted = ((int) instances.stream().filter(ApplicationInstance::isDeleted).count());
        int success = (int) instances.stream().filter(e -> !e.isDeleted())
                .filter(ApplicationInstance::isSuccess)
                .filter(e -> Objects.equals(SUCCESS_MESSAGE, e.getMessage()))
                .count();

        int failed = (int) instances.stream().filter(e -> !e.isDeleted())
                .filter(e -> !Objects.equals(SUCCESS_MESSAGE, e.getMessage()))
                .count();

        int total = instances.size();

        return ApplicationInstanceStatistics.builder()
                .deleted(deleted)
                .success(success)
                .failed(failed)
                .total(total)
                .build();
    }
}
