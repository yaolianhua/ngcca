package io.hotcloud.db.core.registry;

import io.hotcloud.db.core.AbstractEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "registry_images")
@Getter
@Setter
public class RegistryImageEntity extends AbstractEntity {

    @Indexed(unique = true)
    private String name;
    private String value;
}
