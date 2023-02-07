package io.hotcloud.db.core.cluster;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface KubernetesClusterRepository extends PagingAndSortingRepository<KubernetesClusterEntity, String>, CrudRepository<KubernetesClusterEntity, String> {

}
