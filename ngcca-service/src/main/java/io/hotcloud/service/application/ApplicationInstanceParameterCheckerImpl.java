package io.hotcloud.service.application;

import io.fabric8.kubernetes.api.model.Namespace;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.common.model.exception.ResourceConflictException;
import io.hotcloud.common.utils.Validator;
import io.hotcloud.db.model.ApplicationInstanceSource;
import io.hotcloud.kubernetes.client.http.NamespaceClient;
import io.hotcloud.service.application.model.ApplicationForm;
import io.hotcloud.service.application.model.ApplicationInstance;
import io.hotcloud.service.cluster.DatabasedKubernetesClusterService;
import io.hotcloud.service.cluster.KubernetesCluster;
import io.hotcloud.service.security.user.User;
import io.hotcloud.service.security.user.UserApi;
import io.kubernetes.client.openapi.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ApplicationInstanceParameterCheckerImpl implements ApplicationInstanceParameterChecker {

    private final ApplicationInstanceService applicationInstanceService;
    private final DatabasedKubernetesClusterService databasedKubernetesClusterService;
    private final UserApi userApi;
    private final NamespaceClient namespaceApi;

    @Override
    public ApplicationInstance check(ApplicationForm applicationForm) {
        ApplicationInstanceSource applicationInstanceSource = applicationForm.getSource();
        if (Objects.isNull(applicationInstanceSource)) {
            throw new IllegalArgumentException("Application instance source object is null");
        }

        if (!StringUtils.hasText(applicationInstanceSource.getUrl())) {
            throw new IllegalArgumentException("Application source url is null");
        }

        if (ApplicationInstanceSource.Origin.SOURCE_CODE.equals(applicationForm.getSource().getOrigin()) &&
                !StringUtils.hasText(applicationInstanceSource.getGitBranch())) {
                applicationInstanceSource.setGitBranch("master");
        }

        boolean nameValid = Validator.validK8sName(applicationForm.getName());
        if (!nameValid) {
            throw new IllegalArgumentException("Application name is illegal");
        }
        User current = userApi.current();
        String namespace = current.getNamespace();
        Assert.hasText(namespace, String.format("[%s] user cached k8s namespace is null", current.getUsername()));

        KubernetesCluster cluster = databasedKubernetesClusterService.findById(applicationForm.getClusterId());
        if (Objects.isNull(cluster)) {
            throw new PlatformException("cluster not found [" + applicationForm.getClusterId() + "]");
        }

        Namespace readNamespace = namespaceApi.read(cluster.getAgentUrl(), namespace);
        if (Objects.isNull(readNamespace)) {
            try {
                namespaceApi.create(cluster.getAgentUrl(), namespace);
                Log.info(this, null,
                        String.format("[%s] user's k8s namespace create success [%s]", current.getUsername(), namespace));
            } catch (ApiException e) {
                throw new PlatformException("Create [" + current.getUsername() + "] user's k8s namespace error: " + e.getMessage());
            }
        }

        ApplicationInstance applicationInstance = applicationInstanceService.findActiveSucceed(current.getUsername(), applicationForm.getName());
        if (Objects.nonNull(applicationInstance)) {
            throw new ResourceConflictException("Application name [" + applicationForm.getName() + "] is already exist for current user [" + current.getUsername() + "]");
        }

        return ApplicationInstance.builder()
                .clusterId(applicationForm.getClusterId())
                .source(applicationInstanceSource)
                .success(false)
                .replicas(applicationForm.getReplicas())
                .namespace(namespace)
                .user(current.getUsername())
                .name(applicationForm.getName())
                .enableIngressAccess(applicationForm.isEnableIngressAccess())
                .createdAt(Instant.now())
                .targetPorts(String.valueOf(applicationForm.getServerPort()))
                .envs(applicationForm.getEnvs())
                .resource(applicationForm.getResource())
                .build();
    }
}
