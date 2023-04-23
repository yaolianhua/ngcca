package io.hotcloud.server.buildpack;

import io.hotcloud.buildpack.api.clone.GitClonedService;
import io.hotcloud.buildpack.server.clone.GitClonedCollectionQuery;
import io.hotcloud.security.api.user.UserApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author yaolianhua789@gmail.com
 **/
@WebMvcTest(value = GitController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@MockBeans(value = {
        @MockBean(classes = {
                GitClonedService.class,
                GitClonedCollectionQuery.class,
                UserApi.class,
                RestTemplate.class
        })
})
public class GitControllerTest {

    public final static String PATH = "/v1/git";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private GitClonedService gitClonedService;

    @Test
    public void cloneRepository() throws Exception {

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>(8);
        params.set("git_url", "https://github.com/GoogleContainerTools/kaniko.git");

        this.mockMvc.perform(MockMvcRequestBuilders.post(PATH.concat("/clones")).params(params))
                .andDo(print())
                .andExpect(status().isCreated());
        //was invoked one time
        verify(gitClonedService, times(1)).clone("https://github.com/GoogleContainerTools/kaniko.git",
                null, null, null, null);
    }

}
