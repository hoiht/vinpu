package com.vinid.vinpu.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link UserTrackingTimeSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class UserTrackingTimeSearchRepositoryMockConfiguration {

    @MockBean
    private UserTrackingTimeSearchRepository mockUserTrackingTimeSearchRepository;

}
