package io.hotcloud.kubernetes.server.controller;

import io.hotcloud.Result;
import io.hotcloud.kubernetes.server.WebResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping
@Slf4j
public class ProbeController {

    @GetMapping(value = "/livez")
    public ResponseEntity<Result<Void>> livenessProbe() {
        log.debug("livenessProbe request");
        return WebResponse.accepted();
    }

    @GetMapping(value = "/readyz")
    public ResponseEntity<Result<Void>> readinessProbe() {
        log.debug("readinessProbe request");
        return WebResponse.accepted();
    }
}
