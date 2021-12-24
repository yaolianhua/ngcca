package io.hotcloud.kubernetes.api.pod;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yaolianhua789@gmail.com
 **/
@FunctionalInterface
public interface PodLogFetchApi {

    default String getLog(String namespace, String pod) {
        return this.getLog(namespace, pod, null);
    }

    String getLog(String namespace, String pod, Integer tailingLine);

    default List<String> getLogLines(String namespace, String pod, Integer tailingLine) {
        String log = getLog(namespace, pod, tailingLine);
        return Stream.of(log.split("\n")).collect(Collectors.toList());
    }


}
