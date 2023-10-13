package io.hotcloud.service.template;

import io.hotcloud.common.model.exception.PlatformException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
public class TemplateInstanceStatisticsService {

    private final TemplateInstanceService templateInstanceService;

    public TemplateInstanceStatisticsService(TemplateInstanceService templateInstanceService) {
        this.templateInstanceService = templateInstanceService;
    }

    /**
     * Get InstanceTemplateStatistics
     *
     * @param username username
     * @return {@link TemplateInstanceStatistics}
     */
    public TemplateInstanceStatistics userStatistics(String username) {
        if (!StringUtils.hasText(username)) {
            throw new PlatformException("username is missing");
        }

        List<TemplateInstance> list = templateInstanceService.findAll(username);
        return statistics(list);

    }

    public TemplateInstanceStatistics allStatistics() {
        List<TemplateInstance> list = templateInstanceService.findAll();
        return statistics(list);
    }

    public TemplateInstanceStatistics statistics(List<TemplateInstance> templateInstances) {

        int success = (int) templateInstances.stream()
                .filter(TemplateInstance::isSuccess)
                .count();

        int failed = (int) templateInstances.stream()
                .filter(e -> !e.isSuccess())
                .count();

        int total = templateInstances.size();

        return TemplateInstanceStatistics.builder()
                .success(success)
                .failed(failed)
                .total(total)
                .build();
    }
}
