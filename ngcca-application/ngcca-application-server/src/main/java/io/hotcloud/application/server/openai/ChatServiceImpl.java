package io.hotcloud.application.server.openai;

import com.theokanning.openai.Usage;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import io.hotcloud.application.api.openai.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final OpenAiService openAiService;

    public ChatServiceImpl(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @Override
    public String chat(String content) {
        StopWatch watch = new StopWatch(content);
        watch.start();
        log.info("Started at chat request, content={}", content);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(List.of(new ChatMessage(ChatMessageRole.USER.value(), content)))
                .build();
        ChatCompletionResult chatCompletion = openAiService.createChatCompletion(chatCompletionRequest);
        List<ChatCompletionChoice> choices = chatCompletion.getChoices();
        Usage usage = chatCompletion.getUsage();

        watch.stop();
        log.info("End of chat request: cost time '{}s', usage body: {}", watch.getTotalTimeSeconds(), usage);
        return choices.get(0).getMessage().getContent();

    }
}
