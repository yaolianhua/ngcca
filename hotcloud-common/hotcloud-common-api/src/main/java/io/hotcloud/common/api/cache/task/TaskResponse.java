package io.hotcloud.common.api.cache.task;

import lombok.Data;

@Data
public class TaskResponse {

    private boolean success;
    private String message;
    private boolean ignore;

    public static TaskResponse success (String message){
        return new TaskResponse(true, message, false);
    }

    public static TaskResponse ignoreTask (){
        return new TaskResponse(true, null, true);
    }

    public static TaskResponse fail (String message){
        return new TaskResponse(false, message, false);
    }

    public TaskResponse(boolean success, String message, boolean ignore) {
        this.success = success;
        this.message = message;
        this.ignore = ignore;
    }
}
