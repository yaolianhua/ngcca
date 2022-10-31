package io.hotcloud.kubernetes.model.pod;

import lombok.Data;
@Data
public class ImagePullSecret {

    private String name;

    public ImagePullSecret(String name) {
        this.name = name;
    }
}
