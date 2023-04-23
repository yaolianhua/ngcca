package io.hotcloud.module.db.core.application;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface TemplateInstanceRepository extends PagingAndSortingRepository<TemplateInstanceEntity, String>, CrudRepository<TemplateInstanceEntity, String> {

    /**
     * Find entity with the giving {@code user} and {@code name}
     *
     * @param user user's username
     * @param name instance template name. e.g. {@code mongo,redis}
     * @return {@link TemplateInstanceEntity}
     */
    TemplateInstanceEntity findByUserAndName(String user, String name);

    /**
     * Find user's entities
     *
     * @param user user's username
     * @return InstanceTemplateEntity collection
     */
    List<TemplateInstanceEntity> findByUser(String user);

    /**
     * Find enitty with the giving {@code uuid}
     *
     * @param uuid business id
     * @return {@link TemplateInstanceEntity}
     */
    TemplateInstanceEntity findByUuid(String uuid);
}
