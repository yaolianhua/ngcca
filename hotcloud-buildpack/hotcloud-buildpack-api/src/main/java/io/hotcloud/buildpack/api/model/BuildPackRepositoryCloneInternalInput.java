package io.hotcloud.buildpack.api.model;

import io.hotcloud.common.Assert;
import io.hotcloud.common.util.Validator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nullable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@AllArgsConstructor
@Builder
public class BuildPackRepositoryCloneInternalInput {

    /**
     * remote git url. protocol supported http(s) only
     */
    private String remote;
    /**
     * the initial branch to check out when cloning the repository.
     * Can be specified as ref name (<code>refs/heads/master</code>),
     * branch name (<code>master</code>) or tag name
     * (<code>v1.2.3</code>). The default is to use the branch
     * pointed to by the cloned repository's HEAD and can be
     * requested by passing {@code null} or <code>HEAD</code>.
     */
    @Nullable
    private String branch;
    /**
     * the path will be cloned locally
     */
    private String local;

    /**
     * Whether to force cloning, if the specified path is not empty, it will be forcibly deleted and then cloned
     */
    private boolean force;

    /**
     * remote repository username credential
     */

    @Nullable
    private String username;
    /**
     * remote repository password credential
     */
    @Nullable
    private String password;

    public BuildPackRepositoryCloneInternalInput() {
    }

    private final static Pattern CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");

    public String retrieveGitProject() {
        Assert.hasText(remote, "Git url is null", 400);
        Assert.state(!CHINESE_PATTERN.matcher(remote).find(), "Git url contains chinese char", 400);
        Assert.state(Validator.validHTTPGitAddress(remote), "http(s) git url support only", 400);
        String substring = remote.substring(remote.lastIndexOf("/"));
        String originString = substring.substring(1, substring.length() - ".git".length());

        String lowerCaseString = originString.toLowerCase();
        return lowerCaseString.replaceAll("_", "-");
    }

    public String retrievePushImage() {
        String name = retrieveGitProject();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = dateFormat.format(new Date());

        return String.format("%s:%s", name, date);
    }

    public String retrieveImageTarball() {
        return retrievePushImage().replace(":", "-") + ".tar";
    }
}
