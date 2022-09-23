package io.hotcloud.application.server.template;

import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.application.api.template.TemplateInstanceService;
import io.hotcloud.common.api.PageResult;
import io.hotcloud.common.api.Pageable;
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

    private final TemplateInstanceService templateService;

    public InstanceTemplateCollectionQuery(TemplateInstanceService templateService) {
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
    public PageResult<TemplateInstance> pagingQuery(@Nullable String user, @Nullable Boolean success, Pageable pageable) {

        List<TemplateInstance> templates;
        if (StringUtils.hasText(user)) {
            templates = templateService.findAll(user);
        } else {
            templates = templateService.findAll();
        }

        List<TemplateInstance> filtered = filter(templates, success);
        return PageResult.ofPage(filtered, pageable.getPage(), pageable.getPageSize());

    }

    public List<TemplateInstance> filter(List<TemplateInstance> templates, Boolean success) {
        if (success == null) {
            return templates;
        }
        return templates.stream()
                .filter(e -> Objects.equals(e.isSuccess(), success))
                .collect(Collectors.toList());
    }

}
