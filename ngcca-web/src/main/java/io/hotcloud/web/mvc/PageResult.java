package io.hotcloud.web.mvc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    public static <E> PageResult<E> ofSingle(Collection<E> data) {
        return new PageResult<>(200, "success", data, data.size(), 1, data.size());
    }

    @JsonIgnore
    public List<E> getList() {
        return new ArrayList<>(getData());
    }

}
