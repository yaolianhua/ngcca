package io.hotcloud.service.volume;

import io.hotcloud.common.model.PageResult;
import io.hotcloud.common.model.Pageable;
import io.hotcloud.db.entity.VolumeEntity;
import io.hotcloud.db.entity.VolumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class VolumeQueryService {

    private final VolumeRepository volumeRepository;


    public Volumes queryOne(String id) {
        Optional<VolumeEntity> optionalVolume = volumeRepository.findById(id);
        if (optionalVolume.isEmpty()) {
            return null;
        }

        VolumeEntity volumeEntity = optionalVolume.get();
        return Volumes.toVolumes(volumeEntity);
    }

    public PageResult<Volumes> pageQuery(Pageable pageable) {
        Iterable<VolumeEntity> volumeEntityIterable = volumeRepository.findAll();
        List<Volumes> volumes = StreamSupport.stream(volumeEntityIterable.spliterator(), false)
                .map(Volumes::toVolumes)
                .collect(Collectors.toList());
        return PageResult.ofCollectionPage(volumes, pageable);
    }

    public List<Volumes> queryUserVolumes(String username) {
        List<VolumeEntity> volumeEntities = volumeRepository.findByCreateUsername(username);
        return volumeEntities.stream()
                .map(Volumes::toVolumes)
                .collect(Collectors.toList());
    }


}
