package io.hotcloud.web.client;

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
public class RP<E> extends R<Collection<E>> {

    private int total;
    private int page;
    private int pageSize;

    public RP(int code, String message, Collection<E> data, int total, int page, int pageSize) {
        super(code, message, data);
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }

    public static <E> RP<E> ofSingle(Collection<E> data) {
        return new RP<>(200, "success", data, data.size(), 1, data.size());
    }

    @JsonIgnore
    public List<E> getList() {
        return new ArrayList<>(getData());
    }

}
