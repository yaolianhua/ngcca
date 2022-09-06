package io.hotcloud.application.server.core;

import io.hotcloud.application.api.core.*;
import io.hotcloud.common.api.Validator;
import io.hotcloud.common.api.cache.Cache;
import io.hotcloud.common.api.exception.HotCloudResourceConflictException;
import io.hotcloud.security.api.SecurityConstant;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@Order(1)
@RequiredArgsConstructor
class ApplicationInstanceParameterValidityCheckProcessor implements ApplicationInstanceProcessor<ApplicationCreate, ApplicationInstance> {

    private final ApplicationInstanceService applicationInstanceService;
    private final UserApi userApi;
    private final Cache cache;


    @Override
    public ApplicationInstance process(ApplicationCreate applicationForm) {

        ApplicationInstanceSource applicationInstanceSource = applicationForm.getSource();
        if (Objects.isNull(applicationInstanceSource)) {
            throw new IllegalArgumentException("Application instance source object is null");
        }

        if (!StringUtils.hasText(applicationInstanceSource.getUrl())) {
            throw new IllegalArgumentException("Application source url is null");
        }

        if (ApplicationInstanceSource.Origin.SOURCE_CODE.equals(applicationForm.getSource().getOrigin())){
            if (!StringUtils.hasText(applicationInstanceSource.getGitBranch())){
                applicationInstanceSource.setGitBranch("master");
            }
        }

        boolean nameValid = Validator.validK8sName(applicationForm.getName());
        if (!nameValid){
            throw new IllegalArgumentException("Application name is illegal");
        }
        User current = userApi.current();
        String namespace = cache.get(String.format(SecurityConstant.CACHE_NAMESPACE_USER_KEY_PREFIX, current.getUsername()), String.class);
        Assert.hasText(namespace, "User's k8s namespace is null");

        ApplicationInstance applicationInstance = applicationInstanceService.findOne(current.getUsername(), applicationForm.getName());
        if (Objects.nonNull(applicationInstance)){
            throw new HotCloudResourceConflictException("Application name [" + applicationForm.getName() + "] is already exist for current user [" + current.getUsername() + "]");
        }

        return ApplicationInstance.builder()
                .source(applicationInstanceSource)
                .success(false)
                .replicas(applicationForm.getReplicas())
                .namespace(namespace)
                .user(current.getUsername())
                .name(applicationForm.getName())
                .createdAt(LocalDateTime.now())
                .targetPorts(String.valueOf(applicationForm.getServerPort()))
                .envs(applicationForm.getEnvs())
                .build();
    }
}
