package io.hotcloud.db;

import io.hotcloud.common.api.Log;
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
@EnableConfigurationProperties(DatabaseProperties.class)
@EnableMongoRepositories(basePackageClasses = AbstractEntity.class)
@EnableTransactionManagement
public class MongodbDatabaseConfiguration {

    private final DatabaseProperties properties;

    public MongodbDatabaseConfiguration(DatabaseProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void print() {
        String mongoUrl = String.format("mongodb://%s:%s/%s", properties.getHost(), properties.getPort(), properties.getDatabase());
        Log.info(MongodbDatabaseConfiguration.class.getName(), String.format("【Load DB configuration. url='%s'】", mongoUrl));
    }

    @Bean
    public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory databaseFactory) {
        return new MongoTransactionManager(databaseFactory);
    }
}
