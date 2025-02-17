package io.hotcloud.server.controller;

import io.hotcloud.common.model.Result;
import io.hotcloud.common.model.SwaggerBearerAuth;
import io.hotcloud.service.application.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SwaggerBearerAuth
@RequestMapping("/v1/openai")
@RestController
@Tag(name = "ChatGPT api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    @Operation(
            summary = "Creates a completion for the chat message",
            responses = {@ApiResponse(responseCode = "200")},
            parameters = {@Parameter(name = "text content", description = "Chat completion request content")}
    )
    public ResponseEntity<Result<String>> chat(@RequestParam("content") String content) {
        return ResponseEntity.status(HttpStatus.OK).body(Result.ok(chatService.chat(content)));
    }
}
