package io.hotcloud.vendor.buildpack;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildPackStatistics {

    private int success;
    private int failed;
    private int deleted;
    private int total;
}
