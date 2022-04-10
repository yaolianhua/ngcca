package io.hotcloud.buildpack.api.clone;

import io.hotcloud.common.Assert;
import io.hotcloud.common.Validator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
@Builder
@AllArgsConstructor
public class GitCloned {
    private String id;
    private String user;

    private boolean success;
    private String url;
    private String project;
    private String localPath;
    private String branch;
    private boolean force;
    private String username;
    private String password;
    private String error;

    public GitCloned() {
    }

    private final static Pattern CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");

    public static String retrieveGitProject(String remote) {
        Assert.hasText(remote, "Git url is null", 400);
        Assert.state(!CHINESE_PATTERN.matcher(remote).find(), "Git url contains chinese char", 400);
        Assert.state(Validator.validHTTPGitAddress(remote), "http(s) git url support only", 400);
        String substring = remote.substring(remote.lastIndexOf("/"));
        String originString = substring.substring(1, substring.length() - ".git".length());

        String lowerCaseString = originString.toLowerCase();
        return lowerCaseString.replaceAll("_", "-");
    }

    public static String retrievePushImage(String remote) {
        String name = retrieveGitProject(remote);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = dateFormat.format(new Date());

        return String.format("%s:%s", name, date);
    }

    public static String retrieveImageTarball(String remote) {
        return retrievePushImage(remote).replace(":", "-") + ".tar";
    }
}
