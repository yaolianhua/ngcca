package io.hotcloud.service.registry;

import io.hotcloud.db.entity.RegistryImageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistryImage {

    private String name;
    private String value;
    private String tag;

    public static RegistryImage build(RegistryImageEntity entity) {
        return RegistryImage.builder()
                .name(entity.getName())
                .value(entity.getValue())
                .tag(entity.getTag())
                .build();
    }
}
