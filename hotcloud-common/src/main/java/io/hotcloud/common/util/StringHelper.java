package io.hotcloud.common.util;

import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}
