package io.hotcloud.web.statistics;

import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Statistics {

    private String user;
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
