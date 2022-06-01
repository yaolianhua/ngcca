package io.hotcloud.kubernetes.server.controller;

import io.hotcloud.common.api.Result;
import io.hotcloud.common.api.WebResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yaolianhua789@gmail.com
 **/
@RestController
@RequestMapping
@Slf4j
@Tag(name = "HotCloud Probe")
public class ProbeController {

    @GetMapping(value = "/livez")
    @Operation(
            summary = "Liveness Probe",
            responses = {@ApiResponse(responseCode = "202")}
    )
    public ResponseEntity<Result<Void>> livenessProbe() {
        log.debug("livenessProbe request");
        return WebResponse.accepted();
    }

    @GetMapping(value = "/readyz")
    @Operation(
            summary = "Readiness Probe",
            responses = {@ApiResponse(responseCode = "202")}
    )
    public ResponseEntity<Result<Void>> readinessProbe() {
        log.debug("readinessProbe request");
        return WebResponse.accepted();
    }
}
