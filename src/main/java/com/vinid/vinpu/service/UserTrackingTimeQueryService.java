package com.vinid.vinpu.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.vinid.vinpu.domain.UserTrackingTime;
import com.vinid.vinpu.domain.*; // for static metamodels
import com.vinid.vinpu.repository.UserTrackingTimeRepository;
import com.vinid.vinpu.repository.search.UserTrackingTimeSearchRepository;
import com.vinid.vinpu.service.dto.UserTrackingTimeCriteria;
import com.vinid.vinpu.service.dto.UserTrackingTimeDTO;
import com.vinid.vinpu.service.mapper.UserTrackingTimeMapper;

/**
 * Service for executing complex queries for {@link UserTrackingTime} entities in the database.
 * The main input is a {@link UserTrackingTimeCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link UserTrackingTimeDTO} or a {@link Page} of {@link UserTrackingTimeDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class UserTrackingTimeQueryService extends QueryService<UserTrackingTime> {

    private final Logger log = LoggerFactory.getLogger(UserTrackingTimeQueryService.class);

    private final UserTrackingTimeRepository userTrackingTimeRepository;

    private final UserTrackingTimeMapper userTrackingTimeMapper;

    private final UserTrackingTimeSearchRepository userTrackingTimeSearchRepository;

    public UserTrackingTimeQueryService(UserTrackingTimeRepository userTrackingTimeRepository, UserTrackingTimeMapper userTrackingTimeMapper, UserTrackingTimeSearchRepository userTrackingTimeSearchRepository) {
        this.userTrackingTimeRepository = userTrackingTimeRepository;
        this.userTrackingTimeMapper = userTrackingTimeMapper;
        this.userTrackingTimeSearchRepository = userTrackingTimeSearchRepository;
    }

    /**
     * Return a {@link List} of {@link UserTrackingTimeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<UserTrackingTimeDTO> findByCriteria(UserTrackingTimeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<UserTrackingTime> specification = createSpecification(criteria);
        return userTrackingTimeMapper.toDto(userTrackingTimeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link UserTrackingTimeDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<UserTrackingTimeDTO> findByCriteria(UserTrackingTimeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<UserTrackingTime> specification = createSpecification(criteria);
        return userTrackingTimeRepository.findAll(specification, page)
            .map(userTrackingTimeMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(UserTrackingTimeCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<UserTrackingTime> specification = createSpecification(criteria);
        return userTrackingTimeRepository.count(specification);
    }

    /**
     * Function to convert {@link UserTrackingTimeCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<UserTrackingTime> createSpecification(UserTrackingTimeCriteria criteria) {
        Specification<UserTrackingTime> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), UserTrackingTime_.id));
            }
            if (criteria.getStartTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getStartTime(), UserTrackingTime_.startTime));
            }
            if (criteria.getEndTime() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getEndTime(), UserTrackingTime_.endTime));
            }
            if (criteria.getDuration() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDuration(), UserTrackingTime_.duration));
            }
            if (criteria.getRole() != null) {
                specification = specification.and(buildStringSpecification(criteria.getRole(), UserTrackingTime_.role));
            }
            if (criteria.getUserId() != null) {
                specification = specification.and(buildSpecification(criteria.getUserId(),
                    root -> root.join(UserTrackingTime_.user, JoinType.LEFT).get(User_.id)));
            }
        }
        return specification;
    }
}
