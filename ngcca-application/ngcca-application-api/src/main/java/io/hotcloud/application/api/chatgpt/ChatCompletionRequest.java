package io.hotcloud.application.api.chatgpt;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatCompletionRequest {
    private String model;
    private List<Message> messages = new ArrayList<>();

    public static ChatCompletionRequest of(String content) {
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel("gpt-3.5-turbo");
        request.setMessages(List.of(Message.ofUserContent(content)));

        return request;
    }
}
