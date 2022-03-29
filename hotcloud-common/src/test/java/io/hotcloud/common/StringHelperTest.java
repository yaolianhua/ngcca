package io.hotcloud.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
public class StringHelperTest {

    @Test
    public void retrieveQueryParams() {

        String q1 = "param1=abc&param2=def&param2=ghi";
        String q2 = "?one=1&two=2&three=3&three=3";

        Map<String, String> p1 = StringHelper.retrieveQueryParams(q1);
        Assertions.assertEquals(2, p1.size());
        Assertions.assertEquals("abc", p1.get("param1"));
        Assertions.assertEquals("ghi", p1.get("param2"));

        Map<String, String> p2 = StringHelper.retrieveQueryParams(q2);
        Assertions.assertEquals(3, p2.size());
        Assertions.assertEquals("1", p2.get("one"));
        Assertions.assertEquals("3", p2.get("three"));


    }

    @Test
    public void retrieveProjectFromHTTPGitUrl() {

        Assertions.assertEquals("kaniko", StringHelper.retrieveProjectFromHTTPGitUrl("https://github.com/GoogleContainerTools/kaniko.git"));
        Assertions.assertEquals("kaniko", StringHelper.retrieveProjectFromHTTPGitUrl("http://github.com/GoogleContainerTools/kaniko.git"));

        Assertions.assertEquals("devops-thymeleaf", StringHelper.retrieveProjectFromHTTPGitUrl("https://gitee.com/yannanshan/devops-thymeleaf.git"));
        Assertions.assertEquals("dev-ops-thymeleaf", StringHelper.retrieveProjectFromHTTPGitUrl("https://gitee.com/yannanshan/DEV-OPS_thymeleaf.git"));
    }

}
