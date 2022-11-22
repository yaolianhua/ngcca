package io.hotcloud.kubernetes.model.pod;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImagePullSecret {

    private String name;

    public ImagePullSecret(String name) {
        this.name = name;
    }
}
