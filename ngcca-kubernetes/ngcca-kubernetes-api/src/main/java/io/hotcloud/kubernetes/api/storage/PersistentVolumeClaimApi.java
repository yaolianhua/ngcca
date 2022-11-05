package io.hotcloud.kubernetes.api.storage;

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaimList;
import io.hotcloud.kubernetes.model.storage.PersistentVolumeClaimCreateRequest;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.models.V1PersistentVolumeClaim;
import io.kubernetes.client.util.Yaml;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface PersistentVolumeClaimApi {
    /**
     * Create PersistentVolumeClaim from {@code PersistentVolumeClaimCreateRequest}
     *
     * @param request {@link PersistentVolumeClaimCreateRequest}
     * @return {@link PersistentVolumeClaim}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    default PersistentVolumeClaim create(PersistentVolumeClaimCreateRequest request) throws ApiException {
        V1PersistentVolumeClaim v1PersistentVolumeClaim = PersistentVolumeClaimBuilder.build(request);
        String json = Yaml.dump(v1PersistentVolumeClaim);
        return this.create(json);
    }

    /**
     * Create PersistentVolumeClaim from yaml
     *
     * @param yaml kubernetes yaml string
     * @return {@link PersistentVolumeClaim}
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    PersistentVolumeClaim create(String yaml) throws ApiException;

    /**
     * Delete namespaced PersistentVolumeClaim
     *
     * @param namespace             namespace
     * @param persistentVolumeClaim persistentVolumeClaim name
     * @throws ApiException throws {@code ApiException} if the request could not be processed correctly from k8s api server
     */
    void delete(String persistentVolumeClaim, String namespace) throws ApiException;

    /**
     * Read namespaced PersistentVolumeClaim
     *
     * @param namespace namespace
     * @param name      persistentVolumeClaim name
     * @return {@link PersistentVolumeClaim}
     */
    default PersistentVolumeClaim read(String namespace, String name) {
        PersistentVolumeClaimList persistentVolumeClaimList = this.read(namespace);
        return persistentVolumeClaimList.getItems()
                .parallelStream()
                .filter(e -> Objects.equals(e.getMetadata().getName(), name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Read PersistentVolumeClaimList all namespace
     *
     * @return {@link PersistentVolumeClaimList}
     */
    default PersistentVolumeClaimList read() {
        return this.read(null);
    }

    /**
     * Read namespaced PersistentVolumeClaimList
     *
     * @param namespace namespace
     * @return {@link PersistentVolumeClaimList}
     */
    default PersistentVolumeClaimList read(String namespace) {
        return this.read(namespace, Collections.emptyMap());
    }

    /**
     * Read namespaced PersistentVolumeClaimList
     *
     * @param namespace     namespace
     * @param labelSelector label selector
     * @return {@link PersistentVolumeClaimList}
     */
    PersistentVolumeClaimList read(String namespace, Map<String, String> labelSelector);
}
