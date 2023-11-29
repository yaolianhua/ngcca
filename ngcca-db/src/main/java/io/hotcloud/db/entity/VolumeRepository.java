package io.hotcloud.db.entity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface VolumeRepository extends PagingAndSortingRepository<VolumeEntity, String>, CrudRepository<VolumeEntity, String> {

    List<VolumeEntity> findByCreateUsername(String createUsername);
}
