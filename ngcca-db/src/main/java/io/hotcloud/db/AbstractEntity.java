package io.hotcloud.db;

import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

/**
 * @author yaolianhua789@gmail.com
 **/
@Getter
public class AbstractEntity implements Serializable {

    /**
     * -- GETTER --
     *  Returns the identifier of the entity.
     *
     * @return the id
     */
    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    public <T> AbstractEntity toE(T data) {
        BeanUtils.copyProperties(data, this);
        return this;
    }

    public <T> T toT(Class<T> clazz) {
        T t;
        try {
            t = clazz.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(this, t);
            return t;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            return null;
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (this.id == null || obj == null || !(this.getClass().equals(obj.getClass()))) {
            return false;
        }

        AbstractEntity that = (AbstractEntity) obj;

        return this.id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}
