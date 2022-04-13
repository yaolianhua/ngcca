package io.hotcloud.db;

import io.hotcloud.db.core.AbstractEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * @author yaolianhua789@gmail.com
 **/
@Configuration(proxyBeanMethods = false)
@Slf4j
@EnableConfigurationProperties(DatabaseProperties.class)
@ConditionalOnProperty(
        name = DatabaseProperties.PROPERTIES_TYPE_NAME,
        havingValue = "mongodb"
)
@EnableMongoRepositories(basePackageClasses = AbstractEntity.class)
@EnableAutoConfiguration(exclude = {
        RedisRepositoriesAutoConfiguration.class,
        RedisAutoConfiguration.class
})
@EnableTransactionManagement
public class MongodbDatabaseConfiguration {

    private final DatabaseProperties properties;

    public MongodbDatabaseConfiguration(DatabaseProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    public void print() {
        final DatabaseProperties.MongodbProperties mongodb = properties.getMongodb();
        Assert.notNull(mongodb, "Mongodb properties is null");
        log.info("【Load DB Configuration. implementation using mongodb. url='{}'】",
                String.format("mongodb://%s:%s/%s", mongodb.getHost(), mongodb.getPort(), mongodb.getDatabase()));
    }

    @Bean
    public MongoTransactionManager mongoTransactionManager(MongoDatabaseFactory databaseFactory) {
        return new MongoTransactionManager(databaseFactory);
    }
}
