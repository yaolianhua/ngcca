package io.hotcloud.application.api.chatgpt;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface ChatCompletionFeignClient {

    @PostMapping("/v1/chat/completions")
    ChatCompletionResponse chat(@RequestBody ChatCompletionRequest request);
}
