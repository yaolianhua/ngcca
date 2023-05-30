package io.hotcloud.service.cache;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class CacheObjectTest implements Serializable {

    private String name;
    private String value;
    private Map<String, String> map = new HashMap<>();
    private List<CacheObjectTest> cacheObjectTests = new ArrayList<>();

    public CacheObjectTest(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public CacheObjectTest() {
    }

}
