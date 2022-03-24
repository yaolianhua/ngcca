package io.hotcloud.buildpack.server.buildpack;

import io.hotcloud.buildpack.api.AbstractBuildPackApi;
import io.hotcloud.buildpack.api.StorageResourceList;
import io.hotcloud.buildpack.server.BuildPackStorageProperties;
import io.hotcloud.kubernetes.model.NamespaceGenerator;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.Resources;
import io.hotcloud.kubernetes.model.storage.*;
import io.kubernetes.client.util.Yaml;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
    public StorageResourceList storageResourceList() {

        //namespace
        String namespace = NamespaceGenerator.uuidNoDashNamespace("buildpack");
        String pvName = "pv-" + namespace;
        String pvcName = "pvc-" + namespace;
        String storageClass = storageProperties.getStorageClass().getName();
        List<String> accessModes = List.of("ReadWriteOnce");
        Map<String, String> storage = Map.of("storage", storageProperties.getSizeGb() + "Gi");


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
        if (BuildPackStorageProperties.Type.hostPath == storageProperties.getType()) {
            HostPathVolume hostPathVolume = HostPathVolume.of(storageProperties.getHostPath().getPath(), null);
            persistentVolumeSpec.setHostPath(hostPathVolume);
        }
        if (BuildPackStorageProperties.Type.nfs == storageProperties.getType()) {
            NFSVolume nfsVolume = NFSVolume.of(storageProperties.getNfs().getPath(), storageProperties.getNfs().getServer(), false);
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
        String pvYaml = Yaml.dump(persistentVolumeCreateRequest);
        String pvcYaml = Yaml.dump(persistentVolumeClaimCreateRequest);

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
}
