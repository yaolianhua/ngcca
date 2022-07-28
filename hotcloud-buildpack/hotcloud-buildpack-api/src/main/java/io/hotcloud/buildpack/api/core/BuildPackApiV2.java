package io.hotcloud.buildpack.api.core;

public interface BuildPackApiV2 {

    /**
     * Deploy a kaniko job from source
     * <p>this repository(branch) must contain a Dockerfile that can be built directly
     * @param namespace user's k8s namespace
     * @param httpGitUrl http(s) git repository url
     * @param branch repository branch
     */
    BuildPack apply(String namespace, String httpGitUrl, String branch);

    /**
     * Deploy a kaniko job from binary jar package
     * @param namespace user's k8s namespace
     * @param httpUrl http(s) binary package url
     * @param jarStartOptions e.g. "-Xms128m -Xmx512m"
     * @param jarStartArgs e.g. -Dspring.profiles.active=production
     */
    BuildPack apply(String namespace, String httpUrl, String jarStartOptions, String jarStartArgs);

    /**
     * Deploy a kaniko job from binary war package
     * @param namespace user's k8s namespace
     * @param httpUrl http(s) binary package url
     */
    BuildPack apply(String namespace, String httpUrl);

    /**
     * Get kaniko job status
     * @param namespace user's k8s namespace
     * @param job kaniko job name
     * @return {@link KanikoStatus}
     */
    KanikoStatus getStatus(String namespace, String job);

    /**
     * Fetch Kaniko build logs
     * @param namespace user's k8s namespace
     * @param job kaniko job name
     */
    String fetchLog(String namespace, String job);

    enum KanikoStatus {
        //
        Ready,
        Active,
        Succeeded,
        Failed,
        Unknown
    }
}
