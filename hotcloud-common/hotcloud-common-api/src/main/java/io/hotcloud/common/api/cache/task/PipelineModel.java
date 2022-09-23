package io.hotcloud.common.api.cache.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PipelineModel implements Serializable {

    private String id;
    private String name;
    private String firstStage;
    private String lastStage;
    private List<StageModel> stages;
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StageModel implements Serializable{
        private String id;
        private String name;
        private String firstTask;
        private String lastTask;
        private List<TaskModel> tasks;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TaskModel implements Serializable {
        private String id;
        private String name;
    }
}
