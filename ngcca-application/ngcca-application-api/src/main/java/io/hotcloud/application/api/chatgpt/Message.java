package io.hotcloud.application.api.chatgpt;

import lombok.Data;

@Data
public class Message {
    private String role;
    private String content;

    public static Message ofUserContent(String content) {
        final Message message = new Message();
        message.setRole("user");
        message.setContent(content.trim());

        return message;
    }
}
