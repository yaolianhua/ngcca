package io.hotcloud.application.server.processor;

import io.hotcloud.application.api.ApplicationRunnerProcessor;
import io.hotcloud.application.api.template.InstanceTemplateResourceHolder;
import io.hotcloud.application.api.template.Template;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * @author yaolianhua789@gmail.com
 **/
@Component
@Slf4j
@Import(InstanceTemplateResourceHolder.class)
class ApplicationInstanceTemplateRunnerProcessor implements ApplicationRunnerProcessor {

    private final InstanceTemplateResourceHolder holder;

    public ApplicationInstanceTemplateRunnerProcessor(InstanceTemplateResourceHolder holder) {
        this.holder = holder;
    }

    @SneakyThrows
    @Override
    public void process() {
        InputStream inputStream = new ClassPathResource("mongodb.template").getInputStream();
        String mongodb = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining("\n"));
        holder.put(Template.Mongodb, mongodb);
    }
}
