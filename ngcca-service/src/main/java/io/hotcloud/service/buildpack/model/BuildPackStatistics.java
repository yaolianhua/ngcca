package io.hotcloud.service.buildpack.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildPackStatistics implements Serializable {

    private int success;
    private int failed;
    private int deleted;
    private int total;
}
