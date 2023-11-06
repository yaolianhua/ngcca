package io.hotcloud.service.template;

import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.NamespaceClient;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.service.cluster.DatabasedKubernetesClusterService;
import io.hotcloud.service.cluster.KubernetesCluster;
import io.hotcloud.service.security.user.User;
import io.hotcloud.service.security.user.UserApi;
import io.hotcloud.service.template.processor.InstanceTemplateProcessors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@RequiredArgsConstructor
public class DefaultTemplateInstancePlayer implements TemplateInstancePlayer {

    private final InstanceTemplateProcessors instanceTemplateProcessors;
    private final TemplateInstanceService templateInstanceService;
    private final KubectlClient kubectlApi;
    private final NamespaceClient namespaceApi;
    private final UserApi userApi;
    private final DatabasedKubernetesClusterService databasedKubernetesClusterService;

    @Override
    public TemplateInstance play(String clusterId, Template template) {

        User current = userApi.current();
        Assert.notNull(current, "retrieve current user is null");
        String namespace = current.getNamespace();

        KubernetesCluster cluster = databasedKubernetesClusterService.findById(clusterId);
        if (Objects.isNull(cluster)) {
            throw new PlatformException("cluster not found [" + clusterId + "]");
        }
        TemplateInstance templateInstance = instanceTemplateProcessors.process(template, current.getUsername(), namespace);

        templateInstance.setClusterId(clusterId);
        TemplateInstance saved = templateInstanceService.saveOrUpdate(templateInstance);
        Log.info(this, null, String.format("[%s] user's [%s] template [%s] save", current.getUsername(), templateInstance.getName(), saved.getId()));

        try {
            if (namespaceApi.read(cluster.getAgentUrl(), namespace) == null) {
                namespaceApi.create(cluster.getAgentUrl(), namespace);
            }
            kubectlApi.resourceListCreateOrReplace(cluster.getAgentUrl(), namespace, YamlBody.of(templateInstance.getYaml()));
        } catch (Exception ex) {
            saved.setMessage(ex.getMessage());
            templateInstanceService.saveOrUpdate(saved);
            Log.error(this, null, String.format("template [%s] start failure.", saved.getName()));
            return saved;
        }

        return saved;

    }

    @Override
    public void delete(String id) {
        Assert.hasText(id, "Template instance id is null");
        TemplateInstance find = templateInstanceService.findOne(id);
        Assert.notNull(find, "Can not found template [" + id + "]");

        templateInstanceService.delete(id);
        Log.info(this, null, String.format("[%s] template '%s' delete ", find.getName(), id));

        String clusterId = StringUtils.hasText(find.getClusterId()) ? find.getClusterId() : CommonConstant.DEFAULT_CLUSTER_ID;
        KubernetesCluster cluster = databasedKubernetesClusterService.findById(clusterId);

        try {
            if (Objects.isNull(cluster)) {
                throw new PlatformException("cluster not found [" + clusterId + "]");
            }

            kubectlApi.delete(cluster.getAgentUrl(), find.getNamespace(), YamlBody.of(find.getYaml()));
            Log.info(this, null, String.format("[%s] template k8s resource delete, namespace:%s", find.getName(), find.getNamespace()));

            if (StringUtils.hasText(find.getIngress())) {
                kubectlApi.delete(cluster.getAgentUrl(), find.getNamespace(), YamlBody.of(find.getIngress()));
                Log.info(this, null, String.format("[%s] template ingress delete, namespace:%s", find.getName(), find.getNamespace()));
            }

        } catch (Exception e) {
            Log.error(this, null, String.format("%s", e.getMessage()));
        }
    }
}
