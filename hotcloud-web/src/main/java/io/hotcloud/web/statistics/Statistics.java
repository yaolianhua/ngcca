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

    private InstanceTemplateStatistics templates = new InstanceTemplateStatistics();
    private GitClonedStatistics repositories = new GitClonedStatistics();
    private BuildPackStatistics buildPacks = new BuildPackStatistics();
    private ApplicationStatistics applications = new ApplicationStatistics();

    @Data
    public static class InstanceTemplateStatistics {
        private int success;
        private int failed;
        private int total;
    }

    @Data
    public static class ApplicationStatistics {
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
