package io.hotcloud.buildpack.server.buildpack;

import io.hotcloud.buildpack.api.AbstractBuildPackApi;
import io.hotcloud.buildpack.api.SecretResource;
import io.hotcloud.buildpack.api.StorageResourceList;
import io.hotcloud.buildpack.server.BuildPackStorageProperties;
import io.hotcloud.common.Assert;
import io.hotcloud.common.Base64Helper;
import io.hotcloud.kubernetes.api.configurations.SecretBuilder;
import io.hotcloud.kubernetes.api.storage.PersistentVolumeBuilder;
import io.hotcloud.kubernetes.api.storage.PersistentVolumeClaimBuilder;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.Resources;
import io.hotcloud.kubernetes.model.SecretCreateRequest;
import io.hotcloud.kubernetes.model.storage.*;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Slf4j
@Service
public class BuildPackService extends AbstractBuildPackApi {

    private final BuildPackStorageProperties storageProperties;

    public BuildPackService(BuildPackStorageProperties storageProperties) {
        this.storageProperties = storageProperties;
    }

    @Override
    public StorageResourceList storageResourceList(String namespace, String pv, String pvc, Integer sizeGb) {

        Assert.hasText(namespace, "namespace is null", 400);

        String pvName = StringUtils.hasText(pv) ? pv : "pv-" + namespace;
        String pvcName = StringUtils.hasText(pvc) ? pvc : "pvc-" + namespace;
        Integer capacity = null == sizeGb ? storageProperties.getSizeGb() : sizeGb;
        String storageClass = storageProperties.getStorageClass().getName();
        List<String> accessModes = List.of("ReadWriteOnce");
        Map<String, String> storage = Map.of("storage", capacity + "Gi");


        //pv
        PersistentVolumeCreateRequest persistentVolumeCreateRequest = new PersistentVolumeCreateRequest();
        ObjectMetadata pvMetadata = new ObjectMetadata();
        pvMetadata.setName(pvName);
        //nothing affect
        pvMetadata.setNamespace(namespace);
        persistentVolumeCreateRequest.setMetadata(pvMetadata);

        PersistentVolumeSpec persistentVolumeSpec = new PersistentVolumeSpec();
        persistentVolumeSpec.setCapacity(storage);
        persistentVolumeSpec.setAccessModes(accessModes);
        persistentVolumeSpec.setStorageClassName(storageClass);
        persistentVolumeSpec.setVolumeMode(PersistentVolumeSpec.VolumeMode.Filesystem);
        persistentVolumeSpec.setPersistentVolumeReclaimPolicy(PersistentVolumeSpec.ReclaimPolicy.Delete);
        if (BuildPackStorageProperties.Type.hostPath == storageProperties.getType()) {
            HostPathVolume hostPathVolume = HostPathVolume.of(Path.of(storageProperties.getHostPath().getPath(), namespace).toString(), null);
            persistentVolumeSpec.setHostPath(hostPathVolume);
        }
        if (BuildPackStorageProperties.Type.nfs == storageProperties.getType()) {
            NFSVolume nfsVolume = NFSVolume.of(Path.of(storageProperties.getNfs().getPath(), namespace).toString(), storageProperties.getNfs().getServer(), false);
            persistentVolumeSpec.setNfs(nfsVolume);
        }
        PersistentVolumeSpec.ClaimRef claimRef = new PersistentVolumeSpec.ClaimRef();
        claimRef.setNamespaces(namespace);
        claimRef.setName(pvcName);
        persistentVolumeSpec.setClaimRef(claimRef);

        persistentVolumeCreateRequest.setSpec(persistentVolumeSpec);

        //pvc
        PersistentVolumeClaimCreateRequest persistentVolumeClaimCreateRequest = new PersistentVolumeClaimCreateRequest();
        ObjectMetadata pvcMetadata = new ObjectMetadata();
        pvcMetadata.setName(pvcName);
        pvcMetadata.setNamespace(namespace);

        persistentVolumeClaimCreateRequest.setMetadata(pvcMetadata);

        PersistentVolumeClaimSpec persistentVolumeClaimSpec = new PersistentVolumeClaimSpec();
        persistentVolumeClaimSpec.setVolumeMode(PersistentVolumeClaimSpec.VolumeMode.Filesystem);
        persistentVolumeClaimSpec.setStorageClassName(storageClass);
        persistentVolumeClaimSpec.setAccessModes(accessModes);
        persistentVolumeClaimSpec.setResources(Resources.ofRequest(storage));
        persistentVolumeClaimSpec.setVolumeName(pvName);

        persistentVolumeClaimCreateRequest.setSpec(persistentVolumeClaimSpec);

        //yaml
        String pvYaml = Yaml.dump(PersistentVolumeBuilder.build(persistentVolumeCreateRequest));
        String pvcYaml = Yaml.dump(PersistentVolumeClaimBuilder.build(persistentVolumeClaimCreateRequest));

        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder();
        stringBuilder.append(pvYaml);
        stringBuilder.append("---\n");
        stringBuilder.append(pvcYaml);

        return StorageResourceList.builder()
                .resourceListYaml(stringBuilder.toString())
                .namespace(namespace)
                .persistentVolumeClaim(pvcName)
                .persistentVolume(pvName)
                .storageClass(storageClass)
                .sizeGb(storageProperties.getSizeGb())
                .build();
    }

    @Override
    public SecretResource dockersecret(String namespace, String name, String registry, String registryUsername, String registryPassword) {
        Assert.hasText(namespace, "namespace is null", 400);

        name = StringUtils.hasText(name) ? name : "secret-" + namespace;
        Map<String, String> labels = Map.of("k8s-app", namespace);

        SecretCreateRequest request = new SecretCreateRequest();
        request.setImmutable(true);
        request.setType("kubernetes.io/dockerconfigjson");

        String configjson = Base64Helper.dockerconfigjson(registry, registryUsername, registryPassword);
        Map<String, String> data = Map.of(".dockerconfigjson", configjson);
        request.setData(data);

        ObjectMetadata secretMetadata = new ObjectMetadata();
        secretMetadata.setName(name);
        secretMetadata.setNamespace(namespace);
        secretMetadata.setLabels(labels);

        request.setMetadata(secretMetadata);

        String secretYaml = Yaml.dump(SecretBuilder.build(request));

        return SecretResource.builder()
                .data(data)
                .labels(labels)
                .name(name)
                .namespace(namespace)
                .secretResourceYaml(secretYaml)
                .build();
    }
}
