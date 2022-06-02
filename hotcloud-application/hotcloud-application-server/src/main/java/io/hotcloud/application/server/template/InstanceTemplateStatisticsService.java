package io.hotcloud.application.server.template;

import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplateService;
import io.hotcloud.application.api.template.InstanceTemplateStatistics;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
public class InstanceTemplateStatisticsService {

    private final InstanceTemplateService instanceTemplateService;

    public InstanceTemplateStatisticsService(InstanceTemplateService instanceTemplateService) {
        this.instanceTemplateService = instanceTemplateService;
    }

    /**
     * Get InstanceTemplateStatistics
     *
     * @param user user's username
     * @return {@link InstanceTemplateStatistics}
     */
    public InstanceTemplateStatistics statistics(@Nullable String user) {
        boolean hasUser = StringUtils.hasText(user);

        if (hasUser) {
            List<InstanceTemplate> list = instanceTemplateService.findAll(user);
            return statistics(list);
        }

        List<InstanceTemplate> list = instanceTemplateService.findAll();
        return statistics(list);
    }

    public InstanceTemplateStatistics statistics(List<InstanceTemplate> instanceTemplates) {

        int success = (int) instanceTemplates.stream()
                .filter(InstanceTemplate::isSuccess)
                .count();

        int failed = (int) instanceTemplates.stream()
                .filter(e -> !e.isSuccess())
                .count();

        int total = instanceTemplates.size();

        return InstanceTemplateStatistics.builder()
                .success(success)
                .failed(failed)
                .total(total)
                .build();
    }
}
