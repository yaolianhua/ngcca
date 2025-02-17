package io.hotcloud.service.buildpack.model;

import io.hotcloud.common.model.JavaRuntime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class War {
    private String packageUrl;
    private JavaRuntime runtime;
}
