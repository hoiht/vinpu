package com.vinid.vinpu.repository.search;

import com.vinid.vinpu.domain.UserTrackingTime;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link UserTrackingTime} entity.
 */
public interface UserTrackingTimeSearchRepository extends ElasticsearchRepository<UserTrackingTime, Long> {
}
