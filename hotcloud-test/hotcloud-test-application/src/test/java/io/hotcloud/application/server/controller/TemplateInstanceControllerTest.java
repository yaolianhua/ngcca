package io.hotcloud.application.server.controller;

import io.hotcloud.application.api.template.TemplateInstance;
import io.hotcloud.application.api.template.TemplateInstancePlayer;
import io.hotcloud.application.server.template.InstanceTemplateCollectionQuery;
import io.hotcloud.common.api.PageResult;
import io.hotcloud.common.api.Pageable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yaolianhua789@gmail.com
 **/
@WebMvcTest(value = InstanceTemplateController.class)
@MockBeans(value = {
        @MockBean(
                classes = {
                        TemplateInstancePlayer.class,
                        InstanceTemplateCollectionQuery.class
                })
}
)
@ActiveProfiles("application-mvc-test")
public class TemplateInstanceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private InstanceTemplateCollectionQuery collectionQuery;

    @Test
    public void templates() throws Exception {
        List<TemplateInstance> templates = buildTemplates();
        PageResult<TemplateInstance> pageResult = PageResult.ofSingle(templates);
        when(collectionQuery.pagingQuery(null, null, Pageable.of(1, 10)))
                .thenReturn(pageResult);

        try (InputStream inputStream = getClass().getResourceAsStream("instance-template-list.json")) {
            String readJson = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream))).lines()
                    .collect(Collectors.joining());

            this.mockMvc.perform(MockMvcRequestBuilders.get("/v1/instance/templates")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(readJson));
        }
    }

    private List<TemplateInstance> buildTemplates() {
        TemplateInstance templateInstance = TemplateInstance.builder()
                .id("62736396d6ebfb102ce2701b")
                .user("admin")
                .name("rabbitmq")
                .namespace("3b24fe96f5f14d53b67e0082f776047d")
                .message("success")
                .nodePorts("30573,30984")
                .modifiedAt(LocalDateTime.of(2022, 5, 5, 13, 41, 52))
                .targetPorts("5672,15672")
                .service("rabbitmq.3b24fe96f5f14d53b67e0082f776047d.svc.cluster.local")
                .success(true)
                .createdAt(LocalDateTime.of(2022, 5, 5, 13, 41, 42))
                .yaml("apiVersion: apps/v1\nkind: Deployment\nmetadata:\n  name: rabbitmq\n  namespace: 3b24fe96f5f14d53b67e0082f776047d\nspec:\n  selector:\n    matchLabels:\n      app: rabbitmq\n  strategy:\n    type: Recreate\n  template:\n    metadata:\n      labels:\n        app: rabbitmq\n    spec:\n      containers:\n      - image: rabbitmq:3.9-management\n        name: rabbitmq\n        env:\n        - name: RABBITMQ_DEFAULT_PASS\n          value: password\n        - name: RABBITMQ_DEFAULT_USER\n          value: admin\n        ports:\n        - containerPort: 5672\n          name: rabbitmq\n        - containerPort: 15672\n          name: management\n        volumeMounts:\n        - name: rabbitmq-persistent-storage\n          mountPath: /var/lib/rabbitmq\n      volumes:\n      - name: rabbitmq-persistent-storage\n        persistentVolumeClaim:\n          claimName: pvc-rabbitmq-3b24fe96f5f14d53b67e0082f776047d\n---\napiVersion: v1\nkind: PersistentVolume\nmetadata:\n  name: pv-rabbitmq-3b24fe96f5f14d53b67e0082f776047d\nspec:\n  accessModes:\n    - ReadWriteOnce\n  capacity:\n    storage: 10Gi\n  claimRef:\n    kind: PersistentVolumeClaim\n    name: pvc-rabbitmq-3b24fe96f5f14d53b67e0082f776047d\n    namespace: 3b24fe96f5f14d53b67e0082f776047d\n  hostPath:\n    path: /tmp/app/3b24fe96f5f14d53b67e0082f776047d/rabbitmq\n  volumeMode: Filesystem\n  persistentVolumeReclaimPolicy: Retain\n  storageClassName: storage-class-application\n---\napiVersion: v1\nkind: PersistentVolumeClaim\nmetadata:\n  name: pvc-rabbitmq-3b24fe96f5f14d53b67e0082f776047d\n  namespace: 3b24fe96f5f14d53b67e0082f776047d\nspec:\n  accessModes:\n    - ReadWriteOnce\n  resources:\n    requests:\n      storage: 10Gi\n  volumeName: pv-rabbitmq-3b24fe96f5f14d53b67e0082f776047d\n  storageClassName: storage-class-application\n---\napiVersion: v1\nkind: Service\nmetadata:\n  name: rabbitmq\n  namespace: 3b24fe96f5f14d53b67e0082f776047d\nspec:\n  ports:\n  - port: 5672\n    protocol: TCP\n    targetPort: 5672\n    name: rabbitmq\n  - port: 15672\n    protocol: TCP\n    targetPort: 15672\n    name: management\n  selector:\n    app: rabbitmq\n  type: NodePort")
                .build();
        return List.of(templateInstance);
    }
}
