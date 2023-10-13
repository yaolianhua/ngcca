package io.hotcloud.service.template;

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
public class TemplateInstanceStatistics implements Serializable {

    private int success;
    private int failed;
    private int total;
}
