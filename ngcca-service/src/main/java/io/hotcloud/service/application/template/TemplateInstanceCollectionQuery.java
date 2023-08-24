package io.hotcloud.service.application.template;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
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
public class TemplateInstanceCollectionQuery {

    private final TemplateInstanceService templateService;

    public TemplateInstanceCollectionQuery(TemplateInstanceService templateService) {
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
        return PageResult.ofCollectionPage(filtered, pageable);

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
