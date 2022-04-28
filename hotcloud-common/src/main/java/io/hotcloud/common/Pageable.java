package io.hotcloud.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

/**
 * @author yaolianhua789@gmail.com
 **/
@Data
public class Pageable {

    private Integer page;

    private Integer pageSize;

    public Pageable(Integer page, Integer pageSize) {
        page = page == null ? 1 : page;
        pageSize = pageSize == null ? 10 : pageSize;
        this.page = Math.max(page, 1);
        this.pageSize = pageSize < 1 ? 10 : pageSize;
    }

    public static Pageable of(Integer page, Integer pageSize) {
        return new Pageable(page, pageSize);
    }

    @JsonIgnore
    public int offset() {
        return (page - 1) * pageSize;
    }
}
