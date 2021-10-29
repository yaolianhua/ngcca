package io.hotCloud.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Slf4j
public class ProbeController {

    @GetMapping(value = "/livez")
    public ResponseEntity<Void> livenessProbe() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }

    @GetMapping(value = "/readyz")
    public ResponseEntity<Void> readinessProbe() {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
    }
}
