package io.hotcloud.application.server.core;

import io.hotcloud.application.api.IngressTemplateRender;
import io.hotcloud.application.api.core.ApplicationYamlService;
import io.hotcloud.application.api.template.instance.*;
import io.hotcloud.common.model.exception.NGCCACommonException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ApplicationYamlServiceImpl implements ApplicationYamlService {

    @Override
    public String search(String type) {
        if (!StringUtils.hasText(type)) {
            return "";
        }
        if ("minio".equalsIgnoreCase(type)) {
            return MinioTemplate.TEMPLATE;
        }
        if ("rabbitmq".equalsIgnoreCase(type)) {
            return RabbitmqTemplate.TEMPLATE;
        }
        if ("redisinsight".equalsIgnoreCase(type)) {
            return RedisInsightTemplate.TEMPLATE;
        }
        if ("mongodb".equalsIgnoreCase(type)) {
            return MongoTemplate.TEMPLATE;
        }
        if ("mysql".equalsIgnoreCase(type)) {
            return MysqlTemplate.TEMPLATE;
        }
        if ("redis".equalsIgnoreCase(type)) {
            return RedisTemplate.TEMPLATE;
        }
        if ("ingress1rule".equalsIgnoreCase(type)) {
            return IngressTemplateRender.INGRESS_1RULE;
        }
        if ("ingress2rule".equalsIgnoreCase(type)) {
            return IngressTemplateRender.INGRESS_2RULE;
        }

        throw new NGCCACommonException("Unsupported type [" + type + "]", 400);
    }
}
