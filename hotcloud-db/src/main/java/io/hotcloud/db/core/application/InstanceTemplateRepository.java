package io.hotcloud.db.core.application;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * @author yaolianhua789@gmail.com
 **/
public interface InstanceTemplateRepository extends PagingAndSortingRepository<InstanceTemplateEntity, String> {

    /**
     * Find entity with the giving {@code user} and {@code name}
     *
     * @param user user's username
     * @param name instance template name. e.g. {@code mongo,redis}
     * @return {@link InstanceTemplateEntity}
     */
    InstanceTemplateEntity findByUserAndName(String user, String name);
}
