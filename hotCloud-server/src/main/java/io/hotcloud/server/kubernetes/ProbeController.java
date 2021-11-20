package io.hotcloud.server.kubernetes;

import io.hotcloud.core.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.hotcloud.server.WebResponse.accepted;

@RestController
@RequestMapping
@Slf4j
public class ProbeController {

    @GetMapping(value = "/livez")
    public ResponseEntity<Result<Void>> livenessProbe() {
        log.debug("livenessProbe request");
        return accepted();
    }

    @GetMapping(value = "/readyz")
    public ResponseEntity<Result<Void>> readinessProbe() {
        log.debug("readinessProbe request");
        return accepted();
    }
}
