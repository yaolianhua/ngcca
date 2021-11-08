package io.hotcloud.server.kubernetes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.hotcloud.core.kubernetes.deploy.DeploymentCreateApi;
import io.hotcloud.core.kubernetes.deploy.DeploymentDeleteApi;
import io.hotcloud.core.kubernetes.deploy.DeploymentReadApi;
import io.hotcloud.server.kubernetes.deploy.DeploymentController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import static io.hotcloud.server.R.created;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yaolianhua789@gmail.com
 **/
@WebMvcTest(value = DeploymentController.class)
@MockBeans(value = {
        @MockBean(classes = {
                DeploymentCreateApi.class,
                DeploymentReadApi.class,
                DeploymentDeleteApi.class
        })
})
public class DeploymentControllerTest {

    public final static String PATH = "/v1/kubernetes/deployments";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private DeploymentCreateApi deploymentCreateApi;
    @MockBean
    private DeploymentReadApi deploymentReadApi;
    @MockBean
    private DeploymentDeleteApi deploymentDeleteApi;


    @Test
    public void deploymentCreateUseYaml() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("deployment-create.txt");
        String yaml = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());

        InputStream deploymentReadInputStream = getClass().getResourceAsStream("deployment-read.json");
        String deploymentReadJson = new BufferedReader(new InputStreamReader(deploymentReadInputStream)).lines().collect(Collectors.joining());

        Deployment deployment = objectMapper.readValue(deploymentReadJson, Deployment.class);
        when(deploymentCreateApi.deployment(yaml)).thenReturn(deployment);

        String json = objectMapper.writeValueAsString(created(deployment).getBody());

        this.mockMvc.perform(MockMvcRequestBuilders
                .post(PATH.concat("/yaml")).contentType(MediaType.TEXT_PLAIN_VALUE).content(yaml))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(json, true));


    }
}
