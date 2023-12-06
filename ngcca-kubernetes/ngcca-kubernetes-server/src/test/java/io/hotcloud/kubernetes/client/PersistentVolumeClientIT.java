package io.hotcloud.kubernetes.client;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.PersistentVolume;
import io.fabric8.kubernetes.api.model.PersistentVolumeList;
import io.hotcloud.common.model.exception.PlatformException;
import io.hotcloud.kubernetes.ClientIntegrationTestBase;
import io.hotcloud.kubernetes.client.http.KubectlClient;
import io.hotcloud.kubernetes.client.http.PersistentVolumeClient;
import io.hotcloud.kubernetes.model.ObjectMetadata;
import io.hotcloud.kubernetes.model.affinity.NodeSelectorOperator;
import io.hotcloud.kubernetes.model.affinity.NodeSelectorTerm;
import io.hotcloud.kubernetes.model.storage.*;
import io.kubernetes.client.openapi.ApiException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@EnableKubernetesAgentClient
public class PersistentVolumeClientIT extends ClientIntegrationTestBase {

    private static final String HOSTPATH_VOLUME = "jason-hostpath-pv";
    private static final String LOCAL_VOLUME = "jason-local-pv";

    @Autowired
    private PersistentVolumeClient persistentVolumeClient;
    @Autowired
    private KubectlClient kubectlClient;

    @Before
    public void init() throws ApiException {
        createHostPath();
        createLocalPath();
    }

    @After
    public void post() throws ApiException {
        persistentVolumeClient.delete(HOSTPATH_VOLUME);
        persistentVolumeClient.delete(LOCAL_VOLUME);
    }

    @Test
    public void read() {
        PersistentVolumeList readList = persistentVolumeClient.readList(null);
        List<PersistentVolume> items = readList.getItems();
        Assert.assertTrue(items.size() > 0);

        List<String> names = items.stream()
                .map(e -> e.getMetadata().getName()).toList();
        names.forEach(System.out::println);

        PersistentVolume result = persistentVolumeClient.read(HOSTPATH_VOLUME);
        String name = result.getMetadata().getName();
        Assert.assertEquals(HOSTPATH_VOLUME, name);

    }

    void createHostPath() throws ApiException {

        PersistentVolumeCreateRequest createRequest = new PersistentVolumeCreateRequest();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setName(HOSTPATH_VOLUME);

        PersistentVolumeSpec spec = new PersistentVolumeSpec();
        spec.setAccessModes(List.of("ReadWriteOnce"));
        spec.setCapacity(Map.of("storage", "1Gi"));
        spec.setPersistentVolumeReclaimPolicy(PersistentVolumeReclaimPolicy.RECYCLE);
        HostPathVolume hostPathVolume = new HostPathVolume();
        hostPathVolume.setPath("/tmp");
        hostPathVolume.setType("");
        spec.setHostPath(hostPathVolume);
        spec.setStorageClassName("local");

        createRequest.setSpec(spec);
        createRequest.setMetadata(objectMetadata);
        persistentVolumeClient.create(createRequest);
    }

    void createLocalPath() throws ApiException {
        PersistentVolumeCreateRequest request = new PersistentVolumeCreateRequest();
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setName(LOCAL_VOLUME);
        metadata.setLabels(Map.of("type", "local"));
        request.setMetadata(metadata);

        PersistentVolumeSpec spec = new PersistentVolumeSpec();
        spec.setStorageClassName("local-storage");

        PersistentVolumeSpec.ClaimRef claimRef = new PersistentVolumeSpec.ClaimRef();
        claimRef.setName("jason-localpvc");
        claimRef.setNamespaces("default");
        spec.setClaimRef(claimRef);

        spec.setCapacity(Map.of("storage", "1Gi"));
        spec.setAccessModes(List.of("ReadWriteOnce"));

        LocalVolume local = new LocalVolume();
        spec.setLocal(local);


        Node storageNode = kubectlClient.listNode().stream()
                .filter(e -> e.getMetadata().getLabels().containsKey("storage-node/hostname"))
                .findFirst()
                .orElseThrow(() -> new PlatformException("there is no node labeled 'storage-node/hostname'"));
        NodeSelectorTerm.MatchRequirement matchRequirement = new NodeSelectorTerm.MatchRequirement();
        matchRequirement.setKey("storage-node/hostname");
        matchRequirement.setOperator(NodeSelectorOperator.IN);
        matchRequirement.setValues(List.of(storageNode.getMetadata().getName()));

        NodeSelectorTerm nodeSelectorTerm = new NodeSelectorTerm();
        nodeSelectorTerm.setMatchExpressions(List.of(matchRequirement));

        VolumeNodeAffinity.Required required = new VolumeNodeAffinity.Required();
        required.setNodeSelectorTerms(List.of(nodeSelectorTerm));

        VolumeNodeAffinity nodeAffinity = new VolumeNodeAffinity();
        nodeAffinity.setRequired(required);
        spec.setNodeAffinity(nodeAffinity);

        request.setSpec(spec);


        persistentVolumeClient.create(request);
    }

}
