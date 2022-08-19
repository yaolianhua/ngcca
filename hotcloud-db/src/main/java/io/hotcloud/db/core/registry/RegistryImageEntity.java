package io.hotcloud.db.core.registry;

import io.hotcloud.db.core.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.redis.core.RedisHash;

@Document(collection = "registry_images")
@RedisHash("registry_images")
@Getter
@Setter
public class RegistryImageEntity extends AbstractEntity {

    @Indexed(unique = true)
    @org.springframework.data.redis.core.index.Indexed
    private String name;
    private String value;
}
