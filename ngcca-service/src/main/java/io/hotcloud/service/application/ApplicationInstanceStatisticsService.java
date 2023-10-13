package io.hotcloud.service.application;

import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.service.application.model.ApplicationInstance;
import io.hotcloud.service.application.model.ApplicationInstanceStatistics;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
     * @param username username
     * @return {@link ApplicationInstanceStatistics}
     */
    public ApplicationInstanceStatistics userStatistics(String username) {
        if (!StringUtils.hasText(username)) {
            throw new PlatformException("username is missing");
        }

        List<ApplicationInstance> instances = applicationInstanceService.findAll(username);
        return statistics(instances);
    }

    public ApplicationInstanceStatistics allStatistics() {
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
