package io.hotcloud.common.model;

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

    public static <E> PageResult<E> ofCollectionPage(Collection<E> data, Pageable pageable) {
        List<E> pagedCollection = data.stream()
                .skip((pageable.offset()))
                .limit(pageable.getPageSize())
                .collect(toList());
        return new PageResult<>(200, "success", pagedCollection, data.size(), pageable.getPage(), pageable.getPageSize());
    }

    public static <E> PageResult<E> ofSingle(Collection<E> data) {
        return new PageResult<>(200, "success", data, data.size(), 1, data.size());
    }

    @JsonIgnore
    public List<E> getList() {
        return new ArrayList<>(getData());
    }

}
