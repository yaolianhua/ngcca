package io.hotcloud.module.db;

import io.hotcloud.common.log.Event;
import io.hotcloud.common.log.Log;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@AutoConfiguration
@ComponentScan
@Slf4j
@EnableConfigurationProperties(MongodbProperties.class)
@EnableMongoRepositories(basePackageClasses = AbstractEntity.class)
@EnableTransactionManagement
public class MongodbAutoConfiguration {

    private final MongodbProperties properties;

    public MongodbAutoConfiguration(MongodbProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void print() {
        Log.info(this, properties, Event.START, "load mongodb properties");
    }

    @Bean
    public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory databaseFactory) {
        return new MongoTransactionManager(databaseFactory);
    }
}
