package io.hotcloud.web.statistics;

import io.hotcloud.web.user.User;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Statistics {

    private User user;
    private String namespace;

    private InstanceTemplateStatistics templates;
    private GitClonedStatistics repositories;
    private BuildPackStatistics buildPacks;

    @Data
    public static class InstanceTemplateStatistics {
        private int success;
        private int failed;
        private int total;
    }

    @Data
    public static class GitClonedStatistics {
        private int success;
        private int failed;
        private int total;
    }

    @Data
    public static class BuildPackStatistics {
        private int success;
        private int failed;
        private int deleted;
        private int total;
    }
}
