package io.hotcloud.allinone.statistics;

import io.hotcloud.application.server.template.InstanceTemplateCollectionQuery;
import io.hotcloud.buildpack.server.clone.GitClonedCollectionQuery;
import io.hotcloud.buildpack.server.core.BuildPackCollectionQuery;
import io.hotcloud.security.api.user.UserApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author yaolianhua789@gmail.com
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsService {

    private final UserApi userApi;
    private final InstanceTemplateCollectionQuery instanceTemplateCollectionQuery;
    private final GitClonedCollectionQuery gitClonedCollectionQuery;
    private final BuildPackCollectionQuery buildPackCollectionQuery;

}
