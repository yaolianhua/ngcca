package io.hotCloud.server.kubernetes;

import io.hotCloud.core.kubernetes.NamespaceCreateParams;
import io.hotCloud.core.kubernetes.V1NamespaceCreateApi;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
public class NamespaceCreator implements V1NamespaceCreateApi {

    private final CoreV1Api coreV1Api;

    public NamespaceCreator(CoreV1Api coreV1Api) {
        this.coreV1Api = coreV1Api;
    }

    @Override
    public void namespace(NamespaceCreateParams params) throws ApiException {

        List<V1Namespace> namespaceList = coreV1Api.listNamespace("true",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null).getItems();
        List<String> namespaces = namespaceList.stream()
                .filter(e -> Objects.nonNull(e.getMetadata()))
                .map(e -> e.getMetadata().getName())
                .collect(Collectors.toList());

        String name = params.getMetadata().getName();
        if (namespaces.contains(name)){
            log.warn("Namespace '{}' already exists", name);
            return;
        }

        V1Namespace namespace = new V1Namespace();
        V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
        v1ObjectMeta.setName(name);
        v1ObjectMeta.setLabels(params.getMetadata().getLabels());
        v1ObjectMeta.setAnnotations(params.getMetadata().getAnnotations());
        namespace.setMetadata(v1ObjectMeta);

        namespace.setApiVersion("v1");
        namespace.setKind("Namespace");

        V1Namespace v1Namespace = coreV1Api.createNamespace(namespace, "true", null, null);
        log.debug("Namespace '{}' created \n '{}'", name, v1Namespace);
    }
}
