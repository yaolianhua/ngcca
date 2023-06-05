package io.hotcloud.vendor.registry.model;

import io.hotcloud.vendor.registry.model.dockerhub.DockerHub;
import org.springframework.util.Assert;

public final class RegistryUtil {


    private RegistryUtil() {
    }

    /**
     * 将name解析为完整名称
     *
     * @param name e.g. nginx or nginx:latest or library/nginx ...
     * @return 包含仓库地址和镜像标签的完整名称
     */
    public static String resolvedName(String name) {
        Assert.hasText(name, "input name is null");

        String registry = DockerHub.REGISTRY_HOST;
        String namespace = DockerHub.OFFICIAL_IMAGE_PREFIX;
        String tag = "latest";

        String[] parts = name.split("/");
        // nginx, nginx:latest
        if (parts.length == 1) {
            if (name.contains(":")) {
                return String.format("%s/%s/%s", registry, namespace, name);
            }
            return String.format("%s/%s/%s:%s", registry, namespace, name, tag);
        }
        //library/nginx, library/nginx:latest
        if (parts.length == 2) {
            if (parts[1].contains(":")) {
                return String.format("%s/%s/%s", registry, parts[0], parts[1]);
            }
            return String.format("%s/%s/%s:%s", registry, parts[0], parts[1], tag);
        }
        //jasonyao/app/web:latest, harbor.local:5000/jason/app/web, harbor.local:5000/jason/app/web:latest
        int pos0 = name.indexOf("/");
        int pos1 = name.indexOf("/", pos0 + 1);
        String imagePart = name.substring(pos1 + 1);
        if (imagePart.contains(":")) {
            return String.format("%s/%s/%s", parts[0], parts[1], imagePart);
        }

        return String.format("%s/%s/%s:%s", parts[0], parts[1], imagePart, tag);
    }

    /**
     * 从输入名称中获取仓库命名空间
     *
     * @param name 不包含仓库地址 e.g. docker.io/library/nginx:latest or nginx:latest or nginx or harbor.local:5000/jasonyao/xxx/image:latest
     * @return 镜像的命名空间
     */
    public static String getNamespace(String name) {
        return resolvedName(name).split("/")[1];
    }

    /**
     * 从输入名称中获取仓库地址
     *
     * @param name e.g. harbor.local:5000/library/minio:20221001
     * @return 仓库地址
     */
    public static String getRegistry(String name) {
        return resolvedName(name).split("/")[0];
    }

    /**
     * 从输入名称中获取不带有镜像标签的镜像名称
     *
     * @param name e.g. harbor.local:5000/library/minio:20221001
     * @return 不包含镜像标签的镜像
     */
    public static String getImageName(String name) {
        String fullName = resolvedName(name);
        int pos0 = fullName.indexOf("/");
        int pos1 = fullName.indexOf("/", pos0 + 1);
        String imagePart = fullName.substring(pos1 + 1);
        return imagePart.split(":")[0];
    }

    /**
     * 从输入名称中获取不带有镜像标签的镜像名称
     *
     * @param name e.g. harbor.local:5000/library/minio:20221001
     * @return 不包含镜像标签的镜像
     */
    public static String getImageTag(String name) {
        String fullName = resolvedName(name);
        int pos0 = fullName.indexOf("/");
        int pos1 = fullName.indexOf("/", pos0 + 1);
        String imagePart = fullName.substring(pos1 + 1);
        String[] parts = imagePart.split(":");
        return parts.length == 1 ? "latest" : parts[1];

    }


    /**
     * 从输入名称中获取带有命名空间的镜像名称
     *
     * @param name e.g. harbor.local:5000/library/minio:20221001
     * @return 包含有命名空间的镜像名称（不包含标签）
     */
    public static String getNamespacedImageName(String name) {
        String fullname = resolvedName(name);
        //包含标签 e.g. library/minio:20221001
        String namespacedImage = fullname.substring(fullname.indexOf("/") + 1);
        return namespacedImage.split(":")[0];
    }

}
