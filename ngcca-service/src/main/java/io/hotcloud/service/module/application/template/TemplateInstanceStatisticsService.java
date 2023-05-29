package io.hotcloud.service.module.application.template;

import io.hotcloud.module.application.template.TemplateInstance;
import io.hotcloud.module.application.template.TemplateInstanceService;
import io.hotcloud.module.application.template.TemplateInstanceStatistics;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
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
     * @param user user's username
     * @return {@link TemplateInstanceStatistics}
     */
    public TemplateInstanceStatistics statistics(@Nullable String user) {
        boolean hasUser = StringUtils.hasText(user);

        if (hasUser) {
            List<TemplateInstance> list = templateInstanceService.findAll(user);
            return statistics(list);
        }

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
