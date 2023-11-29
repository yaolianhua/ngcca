package io.hotcloud.service.volume;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.storage.StorageClass;
import io.hotcloud.common.log.Log;
import io.hotcloud.common.model.CommonConstant;
import io.hotcloud.common.model.K8sLabel;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.db.entity.VolumeEntity;
import io.hotcloud.db.entity.VolumeRepository;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.PersistentVolumeClaimClient;
import io.hotcloud.kubernetes.client.http.PersistentVolumeClient;
import io.hotcloud.kubernetes.client.http.StorageClassClient;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.Resources;
import io.hotcloud.kubernetes.model.affinity.NodeSelectorTerm;
import io.hotcloud.kubernetes.model.storage.*;
import io.hotcloud.service.cluster.DatabasedKubernetesClusterService;
import io.hotcloud.service.cluster.KubernetesCluster;
import io.hotcloud.service.security.user.User;
import io.hotcloud.service.security.user.UserApi;
import io.kubernetes.client.openapi.ApiException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class VolumeCreateService {

    private final PersistentVolumeClient persistentVolumeClient;
    private final PersistentVolumeClaimClient persistentVolumeClaimClient;
    private final StorageClassClient storageClassClient;
    private final KubectlClient kubectlClient;
    private final DatabasedKubernetesClusterService databasedKubernetesClusterService;
    private final VolumeRepository volumeRepository;
    private final UserApi userApi;

    public Volumes create(String name, Integer gigabytes) {
        Assert.notNull(name, "volume name is null");
        Assert.notNull(gigabytes, "volume size (Gigabytes) is null");
        if (gigabytes > 10) {
            throw new PlatformException("The maximum capacity of the storage volume does not exceed 10Gi");
        }
        User user = userApi.current();
        String prefix = String.format("%s-%s", user.getUsername(), RandomStringUtils.randomAlphabetic(7).toLowerCase());
        String pv = prefix + "-pv";
        String pvc = prefix + "-pvc";
        String namespace = user.getNamespace();


        VolumeEntity volume = new VolumeEntity();
        volume.setName(name);
        volume.setCreateUsername(user.getUsername());
        volume.setType(VolumesType.LOCAL);
        volume.setGigabytes(gigabytes);
        volume.setPersistentVolume(pv);
        volume.setPersistentVolumeClaim(pvc);
        volume.setNamespace(namespace);
        VolumeEntity saved = volumeRepository.save(volume);

        KubernetesCluster cluster = databasedKubernetesClusterService.findById(CommonConstant.DEFAULT_CLUSTER_ID);
        Node storageNode = kubectlClient.listNode(cluster.getAgentUrl()).stream()
                .filter(e -> e.getMetadata().getLabels().containsKey(K8sLabel.STORAGE_NODE))
                .findFirst()
                .orElseThrow(() -> new PlatformException("there is no node labeled 'storage-node/hostname'"));
        try {
            createStorageClassIfNeed(cluster.getAgentUrl());
            createPersistentVolume(gigabytes, cluster.getAgentUrl(), namespace, pv, pvc, storageNode.getMetadata().getName());
            createPersistentVolumeClaim(cluster.getAgentUrl(), namespace, pvc);
        } catch (ApiException e) {
            throw new PlatformException("create volume error: " + e.getMessage());
        }

        return Volumes.toVolumes(saved);

    }

    /**
     * <pre>{@code kind: StorageClass
     * apiVersion: storage.k8s.io/v1
     * metadata:
     *   name: local-storage
     * provisioner: kubernetes.io/no-provisioner
     * volumeBindingMode: WaitForFirstConsumer
     * ---
     * apiVersion: v1
     * kind: PersistentVolume
     * metadata:
     *   name: jenkins-pv-volume
     *   labels:
     *     type: local
     * spec:
     *   storageClassName: local-storage
     *   claimRef:
     *     name: jenkins-pv-claim
     *     namespace: devops-tools
     *   capacity:
     *     storage: 10Gi
     *   accessModes:
     *     - ReadWriteOnce
     *   local:
     *     path: /mnt
     *   nodeAffinity:
     *     required:
     *       nodeSelectorTerms:
     *       - matchExpressions:
     *         - key: kubernetes.io/hostname
     *           operator: In
     *           values:
     *           - worker-node01
     * ---
     * apiVersion: v1
     * kind: PersistentVolumeClaim
     * metadata:
     *   name: jenkins-pv-claim
     *   namespace: devops-tools
     * spec:
     *   storageClassName: local-storage
     *   accessModes:
     *     - ReadWriteOnce
     *   resources:
     *     requests:
     *       storage: 3Gi}</pre>
     */

    private void createPersistentVolumeClaim(String agent, String namespace, String pvc) throws ApiException {
        PersistentVolumeClaimCreateRequest claimCreateRequest = new PersistentVolumeClaimCreateRequest();
        ObjectMetadata pvcObjectMetadata = new ObjectMetadata();
        pvcObjectMetadata.setName(pvc);
        pvcObjectMetadata.setNamespace(namespace);
        claimCreateRequest.setMetadata(pvcObjectMetadata);

        PersistentVolumeClaimSpec claimSpec = new PersistentVolumeClaimSpec();
        claimSpec.setStorageClassName(StorageClassName.LOCAL_STORAGE);
        claimSpec.setAccessModes(List.of("ReadWriteOnce"));
        Resources resources = new Resources();
        resources.setRequests(Map.of("storage", "1Gi"));
        claimSpec.setResources(resources);
        claimCreateRequest.setSpec(claimSpec);

        persistentVolumeClaimClient.create(agent, claimCreateRequest);
    }

    private void createPersistentVolume(Integer gigabytes, String agent, String namespace, String pv, String pvc, String k8sNodeName) throws ApiException {
        PersistentVolumeCreateRequest request = new PersistentVolumeCreateRequest();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setName(pv);
        metadata.setLabels(Map.of("type", VolumesType.LOCAL));
        request.setMetadata(metadata);

        PersistentVolumeSpec spec = new PersistentVolumeSpec();
        spec.setStorageClassName(StorageClassName.LOCAL_STORAGE);

        PersistentVolumeSpec.ClaimRef claimRef = new PersistentVolumeSpec.ClaimRef();
        claimRef.setName(pvc);
        claimRef.setNamespaces(namespace);
        spec.setClaimRef(claimRef);

        spec.setCapacity(Map.of("storage", gigabytes + "Gi"));
        spec.setAccessModes(List.of("ReadWriteOnce"));

        LocalVolume local = new LocalVolume();
        spec.setLocal(local);


        NodeSelectorTerm.MatchRequirement matchRequirement = new NodeSelectorTerm.MatchRequirement();
        matchRequirement.setKey(K8sLabel.STORAGE_NODE);
        matchRequirement.setOperator(NodeSelectorTerm.Operator.In);
        matchRequirement.setValues(List.of(k8sNodeName));

        NodeSelectorTerm nodeSelectorTerm = new NodeSelectorTerm();
        nodeSelectorTerm.setMatchExpressions(List.of(matchRequirement));

        VolumeNodeAffinity.Required required = new VolumeNodeAffinity.Required();
        required.setNodeSelectorTerms(List.of(nodeSelectorTerm));

        VolumeNodeAffinity nodeAffinity = new VolumeNodeAffinity();
        nodeAffinity.setRequired(required);
        spec.setNodeAffinity(nodeAffinity);

        request.setSpec(spec);


        persistentVolumeClient.create(agent, request);
    }

    private void createStorageClassIfNeed(String agent) throws ApiException {
        StorageClass storageClass = storageClassClient.read(agent, StorageClassName.LOCAL_STORAGE);
        if (Objects.isNull(storageClass)) {
            StorageClassCreateRequest storageClassCreateRequest = new StorageClassCreateRequest();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setName(StorageClassName.LOCAL_STORAGE);
            storageClassCreateRequest.setMetadata(objectMetadata);

            storageClassCreateRequest.setProvisioner("kubernetes.io/no-provisioner");
            storageClassCreateRequest.setVolumeBindingMode("WaitForFirstConsumer");
            storageClassClient.create(agent, storageClassCreateRequest);
            Log.info(this, agent, "created storageClass " + StorageClassName.LOCAL_STORAGE);
        }
    }

}
