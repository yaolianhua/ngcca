package io.hotcloud.application.server.chatgpt;

import io.hotcloud.application.api.chatgpt.ChatCompletionFeignClient;
import io.hotcloud.application.api.chatgpt.ChatCompletionRequest;
import io.hotcloud.application.api.chatgpt.ChatCompletionResponse;
import io.hotcloud.application.api.chatgpt.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.util.List;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatCompletionFeignClient chatCompletionFeignClient;

    public ChatServiceImpl(ChatCompletionFeignClient chatCompletionFeignClient) {
        this.chatCompletionFeignClient = chatCompletionFeignClient;
    }

    @Override
    public String chat(String content) {
        StopWatch watch = new StopWatch(content);
        watch.start();
        log.info("Started at chatgpt request, content={}", content);
        ChatCompletionResponse chatCompletionResponse = chatCompletionFeignClient.chat(ChatCompletionRequest.of(content));
        List<ChatCompletionResponse.Choice> choices = chatCompletionResponse.getChoices();
        if (choices.isEmpty()) {
            return "";
        }

        ChatCompletionResponse.Usage usage = chatCompletionResponse.getUsage();
        ChatCompletionResponse.Choice choice = choices.get(0);

        watch.stop();
        log.info("End of chatgpt request: cost time '{}s', usage body: {}", watch.getTotalTimeSeconds(), usage);
        return choice.getMessage().getContent();

    }
}
