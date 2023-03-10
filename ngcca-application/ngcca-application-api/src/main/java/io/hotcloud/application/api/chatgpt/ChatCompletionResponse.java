package io.hotcloud.application.api.chatgpt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatCompletionResponse {
    private long created;
    private Usage usage;
    private String id;
    private List<Choice> choices = new ArrayList<>();
    private String object;
    private String model;

    @Data
    public static class Usage {
        @JsonProperty("completion_tokens")
        private int completionTokens;
        @JsonProperty("prompt_tokens")
        private int promptTokens;
        @JsonProperty("total_tokens")
        private int totalTokens;
    }

    @Data
    public static class Choice {
        @JsonProperty("finish_reason")
        private String finishReason;
        private int index;
        private Message message;

    }

}