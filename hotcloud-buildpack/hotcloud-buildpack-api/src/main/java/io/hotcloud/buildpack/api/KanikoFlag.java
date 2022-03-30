package io.hotcloud.buildpack.api;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface KanikoFlag {

    /**
     * <a href="https://github.com/GoogleContainerTools/kaniko#kaniko-build-contexts">https://github.com/GoogleContainerTools/kaniko#kaniko-build-contexts</a>
     * <p> kaniko's build context is very similar to the build context you would send your Docker daemon for an image build;
     * it represents a directory containing a Dockerfile which kaniko will use to build your image. For example, a COPY command in your Dockerfile should refer to a file in the build context.
     * <p> You will need to store your build context in a place that kaniko can access. Right now, kaniko supports these storage solutions:
     * <ul>
     * <li>GCS Bucket
     * <li>S3 Bucket
     * <li>Azure Blob Storage
     * <li>Local Directory
     * <li>Local Tar
     * <li>Standard Input
     * <li>Git Repository
     * </ul>
     *
     * @return context
     */
    String getContext();

    /**
     * Registry the final image should be pushed to. Set it repeatedly for multiple destinations. {@code gcr.io/$PROJECT/$IMAGE:$TAG>}
     *
     * @return destination
     */
    String getDestination();

    /**
     * Set this flag as --cache=true to opt into caching with kaniko
     *
     * @return cache
     */
    default boolean isCache() {
        return false;
    }

    /**
     * <p>Set this flag to specify a local directory cache for base images. Defaults to /cache.
     * <p>This flag must be used in conjunction with the --cache=true flag.
     *
     * @return cache-dir
     */
    String getCacheDir();

    /**
     * <p>Set this flag to specify a remote repository that will be used to store cached layers.
     * <p>If this flag is not provided, a cache repo will be inferred from the --destination flag. If --destination=gcr.io/kaniko-project/test,
     * then cached layers will be stored in gcr.io/kaniko-project/test/cache.
     * <p>This flag must be used in conjunction with the --cache=true flag.
     *
     * @return cache-repo
     */
    String getCacheRepo();

    /**
     * Cache timeout in hours. Defaults to two weeks.
     *
     * @return cache-ttl
     */
    String getCacheTtl();

    /**
     * Set this flag to clean the filesystem at the end of the build.
     *
     * @return cleanup
     */
    default boolean isCleanup() {
        return true;
    }

    /**
     * Set this to false in order to prevent tar compression for cached layers. This will increase the runtime of the build,
     * but decrease the memory usage especially for large builds.
     * Try to use --compressed-caching=false if your build fails with an out of memory error. Defaults to true
     *
     * @return compressed-caching
     */
    default boolean isCompressedCaching() {
        return true;
    }

    /**
     * Set a sub path within the given --context.
     * <p>Its particularly useful when your context is, for example, a git repository, and you want to build one of its subfolders instead of the root folder
     *
     * @return context-sub-path
     */
    String getContextSubPath();

    /**
     * Set this flag to specify a file in the container. This file will receive the digest of a built image. This can be used to automatically track the exact image built by kaniko.
     * <p>For example, setting the flag to --digest-file=/dev/termination-log will write the digest to that file,
     * which is picked up by Kubernetes automatically as the {{.state.terminated.message}} of the container.
     *
     * @return digest-file
     */
    String getDigestFile();

    /**
     * Path to the dockerfile to be built. (default "Dockerfile")
     *
     * @return dockerfile
     */
    default String getDockerfile() {
        return "Dockerfile";
    }

    /**
     * Force building outside of a container
     *
     * @return force
     */
    default boolean isForce() {
        return false;
    }

    /**
     * Branch to clone if build context is a git repository (default branch=,single-branch=false,recurse-submodules=false)
     *
     * @return git
     */
    String getGit();

    /**
     * Specify a file to save the image name w/ digest of the built image to.
     *
     * @return image-name-with-digest-file
     */
    String getImageNameWithDigestFile();

    /**
     * Specify a file to save the image name w/ image tag and digest of the built image to.
     *
     * @return image-name-tag-with-digest-file
     */
    String getImageNameTagWithDigestFile();

    /**
     * Set this flag if you want to push images to a plain HTTP registry. It is supposed to be used for testing purposes only and should not be used in production!
     *
     * @return insecure
     */
    default boolean isInsecure() {
        return true;
    }

    /**
     * Set this flag if you want to pull images from a plain HTTP registry. It is supposed to be used for testing purposes only and should not be used in production!
     *
     * @return insecure-pull
     */
    default boolean isInsecurePull() {
        return true;
    }

    /**
     * Set this flag to use plain HTTP requests when accessing a registry.
     * It is supposed to be used for testing purposes only and should not be used in production! You can set it multiple times for multiple registries.
     *
     * @return insecure-registry
     */
    String getInsecureRegistry();

    /**
     * Set this flag as --label key=value to set some metadata to the final image. This is equivalent as using the LABEL within the Dockerfile.
     *
     * @return label
     */
    String getLabel();

    /**
     * Set this flag as --log-format={@code <text|color|json>} to set the log format. Defaults to color.
     *
     * @return log-format
     */
    default String getLogFormat() {
        return "color";
    }

    /**
     * Set this flag as --log-timestamp={@code <true|false>} to add timestamps to {@code <text|color>} log format. Defaults to false.
     *
     * @return log-timestamp
     */
    default boolean isLogTimestamp() {
        return false;
    }

    /**
     * Set this flag if you only want to build the image, without pushing to a registry.
     *
     * @return no-push
     */
    default boolean isNoPush() {
        return false;
    }

    /**
     * Set this flag to the number of retries that should happen for the push of an image to a remote destination. Defaults to 0.
     *
     * @return push-retry
     */
    default int getPushRetry() {
        return 3;
    }

    /**
     * Set this flag to provide a certificate for TLS communication with a given registry.
     * <p>Expected format is my.registry.url=/path/to/the/certificate.cert
     *
     * @return registry-certificate
     */
    String getRegistryCertificate();

    /**
     * Set this flag if you want to use a registry mirror instead of the default index.docker.io. You can use this flag more than once, if you want to set multiple mirrors.
     * If an image is not found on the first mirror, Kaniko will try the next mirror(s), and at the end fallback on the default registry.
     * <p>Expected format is mirror.gcr.io for example.
     *
     * <p>Note that you can't specify a URL with scheme for this flag. Some valid options are:
     * <ul>
     * <li>mirror.gcr.io
     * <li>127.0.0.1
     * <li>192.168.0.1:5000
     * <li>mycompany-docker-virtual.jfrog.io
     * </ul>
     *
     * @return registry-mirror
     */
    String getRegistryMirror();

    /**
     * Set this flag to strip timestamps out of the built image and make it reproducible.
     *
     * @return reproducible
     */
    default boolean isReproducible() {
        return false;
    }

    /**
     * This flag takes a single snapshot of the filesystem at the end of the build, so only one layer will be appended to the base image.
     *
     * @return single-snapshot
     */
    default boolean isSingleSnapshot() {
        return false;
    }

    /**
     * Set this flag to skip TLS certificate validation when pushing to a registry. It is supposed to be used for testing purposes only and should not be used in production!
     *
     * @return skip-tls-verify
     */
    default boolean isSkipTlsVerify() {
        return true;
    }

    /**
     * Set this flag to skip TLS certificate validation when pulling from a registry. It is supposed to be used for testing purposes only and should not be used in production!
     *
     * @return skip-tls-verify-pull
     */
    default boolean isSkipTlsVerifyPull() {
        return true;
    }

    /**
     * Set this flag to skip TLS certificate validation when accessing a registry. It is supposed to be used for testing purposes only and should not be used in production! You can set it multiple times for multiple registries.
     *
     * @return skip-tls-verify-registry
     */
    default boolean isSkipTlsVerifyRegistry() {
        return true;
    }

    /**
     * This flag builds only used stages if defined to true. Otherwise it builds by default all stages, even the unnecessaries ones until it reaches the target stage / end of Dockerfile
     *
     * @return skip-unused-stages
     */
    default boolean isSkipUnusedStages() {
        return false;
    }

    /**
     * You can set the --snapshotMode={@code <full (default), redo, time>} flag to set how kaniko will snapshot the filesystem.
     * <p>If --snapshotMode=full is set, the full file contents and metadata are considered when snapshotting. This is the least performant option, but also the most robust.
     * <p>If --snapshotMode=redo is set, the file mtime, size, mode, owner uid and gid will be considered when snapshotting. This may be up to 50% faster than "full", particularly if your project has a large number files.
     * <p>If --snapshotMode=time is set, only file mtime will be considered when snapshotting (see limitations related to mtime).
     *
     * @return snapshotMode
     */
    default String getSnapshotMode() {
        return "full";
    }

    /**
     * Set this flag as --tarPath={@code <path>} to save the image as a tarball at path. You need to set --destination as well (for example --destination=image). If you want to save the image as tarball only you also need to set --no-push.
     *
     * @return tarPath
     */
    String getTarPath();

    /**
     * Set this flag to indicate which build stage is the target build stage.
     *
     * @return target
     */
    String getTarget();

    /**
     * Use the experimental run implementation for detecting changes without requiring file system snapshots. In some cases, this may improve build performance by 75%.
     *
     * @return use-new-run
     */
    default boolean isUseNewRun() {
        return true;
    }

    /**
     * Set this flag as --verbosity={@code <panic|fatal|error|warn|info|debug|trace>} to set the logging level. Defaults to info.
     *
     * @return verbosity
     */
    default String getVerbosity() {
        return "debug";
    }

    /**
     * de
     * Ignore /var/run when taking image snapshot. Set it to false to preserve /var/run/* in destination image. (Default true).
     *
     * @return ignore-var-run
     */
    default boolean isIgnoreVarRun() {
        return true;
    }

    /**
     * Set this flag as --ignore-path={@code <path>} to ignore path when taking an image snapshot. Set it multiple times for multiple ignore paths.
     *
     * @return ignore-path
     */
    String getIgnorePath();

    /**
     * Set this flag to the number of retries that should happen for the extracting an image filesystem. Defaults to 0.
     *
     * @return image-fs-extract-retry
     */
    default int getImageFsExtractRetry() {
        return 3;
    }

    /**
     * Convert kaniko flags, output as container args mapping
     *
     * @return mapping of kaniko args
     */
    Map<String, String> resolvedArgs();
}
