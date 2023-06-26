package io.hotcloud.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import io.hotcloud.module.application.template.TemplateDefinition;
import io.hotcloud.module.application.template.TemplateDefinitionService;
import io.hotcloud.service.runner.RunnerProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SystemTemplateDefinitionProcessor implements RunnerProcessor {

    public static final String TEMPLATE_DEFINITION;

    static {
        try {
            TEMPLATE_DEFINITION = new BufferedReader(new InputStreamReader(new ClassPathResource("template_definition.json").getInputStream())).lines().collect(Collectors.joining("\n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final TemplateDefinitionService templateDefinitionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SystemTemplateDefinitionProcessor(TemplateDefinitionService templateDefinitionService) {
        this.templateDefinitionService = templateDefinitionService;
    }

    @Override
    public void execute() {
        List<TemplateDefinition> templateDefinitions;
        try {
            templateDefinitions = objectMapper.readValue(TEMPLATE_DEFINITION, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        for (TemplateDefinition definition : templateDefinitions) {
            try {
                templateDefinitionService.saveOrUpdate(definition);
            } catch (Exception e) {
                Log.error(this, null, Event.EXCEPTION, "[" + definition.getName() + "] template definition save or update error: " + e.getMessage());
            }

        }
    }
}
