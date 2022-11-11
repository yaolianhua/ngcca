package io.hotcloud.db;

import io.hotcloud.common.model.Log;
import io.hotcloud.db.core.AbstractEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;

@Configuration(proxyBeanMethods = false)
@Slf4j
@EnableConfigurationProperties(NgccaMongodbProperties.class)
@EnableMongoRepositories(basePackageClasses = AbstractEntity.class)
@EnableTransactionManagement
public class NgccaMongodbConfiguration {

    private final NgccaMongodbProperties properties;

    public NgccaMongodbConfiguration(NgccaMongodbProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void print() {
        String mongoUrl = String.format("mongodb://%s:%s/%s", properties.getHost(), properties.getPort(), properties.getDatabase());
        Log.info(NgccaMongodbConfiguration.class.getName(), String.format("【Load DB configuration. url='%s'】", mongoUrl));
    }

    @Bean
    public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory databaseFactory) {
        return new MongoTransactionManager(databaseFactory);
    }
}
