package io.hotCloud.core.kubernetes.container;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Builder
@Data
public class Exec {
    @Builder.Default
    private List<String> command = new ArrayList<>();
}
