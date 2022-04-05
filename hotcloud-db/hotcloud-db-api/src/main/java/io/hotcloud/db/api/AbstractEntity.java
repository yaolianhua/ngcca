package io.hotcloud.db.api;

import org.springframework.data.annotation.Id;

import java.io.Serializable;

/**
 * @author yaolianhua789@gmail.com
 **/
public class AbstractEntity implements Serializable {

    @Id
    private String id;

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
