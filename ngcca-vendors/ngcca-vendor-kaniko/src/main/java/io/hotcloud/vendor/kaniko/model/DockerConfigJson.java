package io.hotcloud.vendor.kaniko.model;

import lombok.Data;

@Data
public class DockerConfigJson {
    /**
     * 仓库地址 e.g. harbor.local:5000
     */
    private String registry;
    /**
     * 仓库授权用户
     */
    private String user;
    /**
     * 仓库授权用户访问密码
     */
    private String password;

    public static DockerConfigJson of(String registry, String user, String password) {
        DockerConfigJson dockerConfigJson = new DockerConfigJson();
        dockerConfigJson.setRegistry(registry);
        dockerConfigJson.setUser(user);
        dockerConfigJson.setPassword(password);

        return dockerConfigJson;
    }
}
