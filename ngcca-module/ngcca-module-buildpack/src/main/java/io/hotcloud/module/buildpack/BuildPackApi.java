package io.hotcloud.module.buildpack;

public interface BuildPackApi {
    /**
     * Deploy kaniko job with the giving {@code buildImage}
     *
     * @param namespace  k8s namespace
     * @param buildImage {@link BuildImage}
     * @return {@link BuildPack}
     */
    BuildPack apply(String namespace, BuildImage buildImage);

    /**
     * Get kaniko job status
     *
     * @param namespace user's k8s namespace
     * @param job       kaniko job name
     * @return {@link JobState}
     */
    JobState getStatus(String namespace, String job);

    /**
     * Fetch Kaniko build logs
     *
     * @param namespace user's k8s namespace
     * @param job       kaniko job name
     */
    String fetchLog(String namespace, String job);
}
