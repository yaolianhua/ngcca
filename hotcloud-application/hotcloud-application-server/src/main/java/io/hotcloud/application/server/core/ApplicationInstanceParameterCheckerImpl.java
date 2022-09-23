package io.hotcloud.application.server.core;

import io.fabric8.kubernetes.api.model.Namespace;
import io.hotcloud.application.api.core.*;
import io.hotcloud.common.api.Log;
import io.hotcloud.common.api.Validator;
import io.hotcloud.common.api.cache.Cache;
import io.hotcloud.common.api.exception.HotCloudException;
import io.hotcloud.common.api.exception.HotCloudResourceConflictException;
import io.hotcloud.kubernetes.api.namespace.NamespaceApi;
import io.hotcloud.security.api.user.User;
import io.hotcloud.security.api.user.UserApi;
import io.kubernetes.client.openapi.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

import static io.hotcloud.common.api.CommonConstant.CK_NAMESPACE_USER_KEY_PREFIX;

@Component
@RequiredArgsConstructor
public class ApplicationInstanceParameterCheckerImpl implements ApplicationInstanceParameterChecker {

    private final ApplicationInstanceService applicationInstanceService;
    private final UserApi userApi;
    private final Cache cache;
    private final NamespaceApi namespaceApi;
    @Override
    public ApplicationInstance check(ApplicationForm applicationForm) {
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
        String namespace = cache.get(String.format(CK_NAMESPACE_USER_KEY_PREFIX, current.getUsername()), String.class);
        Assert.hasText(namespace, String.format("[%s] user cached k8s namespace is null", current.getUsername()));

        Namespace readNamespace = namespaceApi.read(namespace);
        if (Objects.isNull(readNamespace)) {
            try {
                namespaceApi.create(namespace);
                Log.info(ApplicationInstanceParameterCheckerImpl.class.getName(),
                        String.format("[%s] user's k8s namespace create success [%s]", current.getUsername(), namespace));
            } catch (ApiException e) {
                throw new HotCloudException("Create [" + current.getUsername() + "] user's k8s namespace error: " + e.getMessage());
            }
        }

        ApplicationInstance applicationInstance = applicationInstanceService.findActiveSucceed(current.getUsername(), applicationForm.getName());
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
                .canHttp(applicationForm.isCanHttp())
                .createdAt(LocalDateTime.now())
                .targetPorts(String.valueOf(applicationForm.getServerPort()))
                .envs(applicationForm.getEnvs())
                .build();
    }
}
