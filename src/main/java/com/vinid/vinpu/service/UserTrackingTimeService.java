package com.vinid.vinpu.service;

import com.vinid.vinpu.domain.UserTrackingTime;
import com.vinid.vinpu.repository.UserTrackingTimeRepository;
import com.vinid.vinpu.repository.search.UserTrackingTimeSearchRepository;
import com.vinid.vinpu.service.dto.UserTrackingTimeDTO;
import com.vinid.vinpu.service.mapper.UserTrackingTimeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing {@link UserTrackingTime}.
 */
@Service
@Transactional
public class UserTrackingTimeService {

    private final Logger log = LoggerFactory.getLogger(UserTrackingTimeService.class);

    private final UserTrackingTimeRepository userTrackingTimeRepository;

    private final UserTrackingTimeMapper userTrackingTimeMapper;

    private final UserTrackingTimeSearchRepository userTrackingTimeSearchRepository;

    public UserTrackingTimeService(UserTrackingTimeRepository userTrackingTimeRepository, UserTrackingTimeMapper userTrackingTimeMapper, UserTrackingTimeSearchRepository userTrackingTimeSearchRepository) {
        this.userTrackingTimeRepository = userTrackingTimeRepository;
        this.userTrackingTimeMapper = userTrackingTimeMapper;
        this.userTrackingTimeSearchRepository = userTrackingTimeSearchRepository;
    }

    /**
     * Save a userTrackingTime.
     *
     * @param userTrackingTimeDTO the entity to save.
     * @return the persisted entity.
     */
    public UserTrackingTimeDTO save(UserTrackingTimeDTO userTrackingTimeDTO) {
        log.debug("Request to save UserTrackingTime : {}", userTrackingTimeDTO);
        UserTrackingTime userTrackingTime = userTrackingTimeMapper.toEntity(userTrackingTimeDTO);
        userTrackingTime = userTrackingTimeRepository.save(userTrackingTime);
        UserTrackingTimeDTO result = userTrackingTimeMapper.toDto(userTrackingTime);
        userTrackingTimeSearchRepository.save(userTrackingTime);
        return result;
    }

    /**
     * Get all the userTrackingTimes.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UserTrackingTimeDTO> findAll() {
        log.debug("Request to get all UserTrackingTimes");
        return userTrackingTimeRepository.findAll().stream()
            .map(userTrackingTimeMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one userTrackingTime by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<UserTrackingTimeDTO> findOne(Long id) {
        log.debug("Request to get UserTrackingTime : {}", id);
        return userTrackingTimeRepository.findById(id)
            .map(userTrackingTimeMapper::toDto);
    }

    /**
     * Delete the userTrackingTime by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete UserTrackingTime : {}", id);
        userTrackingTimeRepository.deleteById(id);
        userTrackingTimeSearchRepository.deleteById(id);
    }

    /**
     * Search for the userTrackingTime corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<UserTrackingTimeDTO> search(String query) {
        log.debug("Request to search UserTrackingTimes for query {}", query);
        return StreamSupport
            .stream(userTrackingTimeSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(userTrackingTimeMapper::toDto)
        .collect(Collectors.toList());
    }
}
