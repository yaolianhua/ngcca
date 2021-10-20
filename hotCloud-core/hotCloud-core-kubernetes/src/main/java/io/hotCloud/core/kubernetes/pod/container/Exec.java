package io.hotCloud.core.kubernetes.pod.container;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Exec {
    private List<String> command = new ArrayList<>();
}
