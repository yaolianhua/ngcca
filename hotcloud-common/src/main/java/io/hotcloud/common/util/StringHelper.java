package io.hotcloud.common.util;

import io.hotcloud.common.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author yaolianhua789@gmail.com
 **/
public final class StringHelper {

    private StringHelper() {
    }

    public static Map<String, String> retrieveQueryParams(String query) {

        if (!StringUtils.hasText(query)) {
            return Collections.emptyMap();
        }
        String decode = URLDecoder.decode(query, StandardCharsets.UTF_8);
        if (decode.startsWith("?")) {
            decode = decode.substring(1);
        }
        MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUriString("http://localhost/query?" + decode)
                .build()
                .getQueryParams();

        Map<String, String> params = new HashMap<>(32);
        for (Map.Entry<String, List<String>> entry : queryParams.entrySet()) {
            for (String value : entry.getValue()) {
                params.put(entry.getKey(), value);
            }
        }

        return params;
    }

    final static Pattern CHINESE_PATTERN = Pattern.compile("[\u4e00-\u9fa5]");

    public static String retrieveProjectFromHTTPGitUrl(String gitUrl) {
        Assert.state(!CHINESE_PATTERN.matcher(gitUrl).find(), "Git url contains chinese char", 400);
        Assert.state(Validator.validHTTPGitAddress(gitUrl), "http(s) git url support only", 400);
        String substring = gitUrl.substring(gitUrl.lastIndexOf("/"));
        String originString = substring.substring(1, substring.length() - ".git".length());

        String lowerCaseString = originString.toLowerCase();
        return lowerCaseString.replaceAll("_", "-");
    }

    public static String generatePushedImage(String gitUrl) {
        String name = retrieveProjectFromHTTPGitUrl(gitUrl);
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = dateFormat.format(new Date());

        return String.format("%s:%s", name, date);
    }

    public static String generateImageTarball(String gitUrl) {
        String pushedImage = generatePushedImage(gitUrl);
        return pushedImage.replace(":", "-") + ".tar";
    }
}
