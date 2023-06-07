package io.hotcloud.module.application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationInstanceStatistics {

    private int success;
    private int failed;
    private int deleted;
    private int total;
}
