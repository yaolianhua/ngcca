package io.hotcloud.kubernetes.server.controller;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.hotcloud.common.Result;
import io.hotcloud.kubernetes.api.equianlent.KubectlApi;
import io.hotcloud.kubernetes.model.YamlBody;
import io.hotcloud.kubernetes.server.WebResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping("/v1/kubernetes/equivalents")
public class KubectlController {

    private final KubectlApi kubectlApi;

    public KubectlController(KubectlApi kubectlApi) {
        this.kubectlApi = kubectlApi;
    }

    @PostMapping
    public ResponseEntity<Result<List<HasMetadata>>> resourceListCreateOrReplace(@RequestParam(value = "namespace",required = false) String namespace,
                                                         @RequestBody YamlBody yaml) {
        List<HasMetadata> hasMetadata = kubectlApi.apply(namespace, yaml.getYaml());
        return WebResponse.created(hasMetadata);
    }

    @DeleteMapping
    public ResponseEntity<Result<Boolean>> resourceListDelete(@RequestParam(value = "namespace", required = false) String namespace,
                                                            @RequestBody YamlBody yaml) {
        Boolean delete = kubectlApi.delete(namespace, yaml.getYaml());
        return WebResponse.accepted(delete);
    }

}
