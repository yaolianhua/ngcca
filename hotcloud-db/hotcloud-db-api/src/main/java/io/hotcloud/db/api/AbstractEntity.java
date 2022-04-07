package io.hotcloud.db.api;

import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

/**
 * @author yaolianhua789@gmail.com
 **/
public class AbstractEntity implements Serializable {

    @Id
    private String id;

    public <T> AbstractEntity copyToEntity(T data) {
        BeanUtils.copyProperties(data, this);
        return this;
    }

    public <T> T toT(Class<T> clazz) {
        T t;
        try {
            t = clazz.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(this, t);
            return t;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Returns the identifier of the entity.
     *
     * @return the id
     */
    public String getId() {
        return id;
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
