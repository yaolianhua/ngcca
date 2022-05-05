package io.hotcloud.application.server.template;

import io.hotcloud.application.api.template.InstanceTemplate;
import io.hotcloud.application.api.template.InstanceTemplateService;
import io.hotcloud.common.PageResult;
import io.hotcloud.common.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
public class InstanceTemplateCollectionQuery {

    private final InstanceTemplateService templateService;

    public InstanceTemplateCollectionQuery(InstanceTemplateService templateService) {
        this.templateService = templateService;
    }

    /**
     * Paging query all instance template with giving parameter
     *
     * @param user     user's username
     * @param success  whether is success
     * @param pageable {@link Pageable}
     * @return paged instance template collection
     */
    public PageResult<InstanceTemplate> pagingQuery(@Nullable String user, @Nullable Boolean success, Pageable pageable) {

        List<InstanceTemplate> templates;
        if (StringUtils.hasText(user)) {
            templates = templateService.findAll(user);
        } else {
            templates = templateService.findAll();
        }

        List<InstanceTemplate> filtered = filter(templates, success);
        return PageResult.ofPage(filtered, pageable.getPage(), pageable.getPageSize());

    }

    public List<InstanceTemplate> filter(List<InstanceTemplate> templates, Boolean success) {
        if (success == null) {
            return templates;
        }
        return templates.stream()
                .filter(e -> Objects.equals(e.isSuccess(), success))
                .collect(Collectors.toList());
    }

}
