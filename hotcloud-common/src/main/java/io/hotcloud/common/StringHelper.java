package io.hotcloud.common;

import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public static String retrieveProjectFromHTTPGitUrl(String gitUrl) {
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
