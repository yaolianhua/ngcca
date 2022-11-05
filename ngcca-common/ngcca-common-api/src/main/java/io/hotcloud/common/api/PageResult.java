package io.hotcloud.common.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author yaolianhua789@gmail.com
 **/
@Getter
@Setter
public class PageResult<E> extends Result<Collection<E>> {

    private int total;
    private int page;
    private int pageSize;

    public PageResult(int code, String message, Collection<E> data, int total, int page, int pageSize) {
        super(code, message, data);
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }

    public static <E> PageResult<E> ofPage(Collection<E> data, int page, int pageSize) {
        List<E> paged = paging(data, page, pageSize);
        return new PageResult<>(200, "success", paged, data.size(), page, pageSize);
    }

    public static <E> PageResult<E> ofSingle(Collection<E> data) {
        return new PageResult<>(200, "success", data, data.size(), 1, data.size());
    }

    public static <T> List<T> paging(Collection<T> data, int page, int pageSize) {

        page = Math.max(page, 1);
        pageSize = pageSize < 1 ? 10 : pageSize;
        return data.stream().skip((((long) (page - 1) * pageSize))).limit(pageSize).collect(toList());

    }

    @JsonIgnore
    public List<E> getList() {
        return new ArrayList<>(getData());
    }

}
