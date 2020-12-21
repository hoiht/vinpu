package com.vinid.vinpu.repository.search;

import com.vinid.vinpu.domain.Order;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


/**
 * Spring Data Elasticsearch repository for the {@link Order} entity.
 */
public interface OrderSearchRepository extends ElasticsearchRepository<Order, Long> {
}
