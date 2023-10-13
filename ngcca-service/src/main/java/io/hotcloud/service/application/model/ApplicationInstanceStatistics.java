package io.hotcloud.service.application.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationInstanceStatistics implements Serializable {

    private int success;
    private int failed;
    private int deleted;
    private int total;
}
