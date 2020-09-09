package com.vinid.vinpu.service;

import com.vinid.vinpu.domain.User;
import com.vinid.vinpu.domain.UserTrackingTime;
import com.vinid.vinpu.repository.UserRepository;
import com.vinid.vinpu.repository.UserTrackingTimeRepository;
import com.vinid.vinpu.repository.search.UserTrackingTimeSearchRepository;
import com.vinid.vinpu.service.dto.UserTrackingTimeDTO;
import com.vinid.vinpu.service.mapper.UserMapper;
import com.vinid.vinpu.service.mapper.UserTrackingTimeMapper;
import com.vinid.vinpu.service.utils.RedisService;
import com.vinid.vinpu.web.rest.vm.RedisUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
    
    private final UserRepository userRepository;
    
    private final RedisService redisService;
    
    private final UserMapper userMapper;

    public UserTrackingTimeService(UserTrackingTimeRepository userTrackingTimeRepository, UserTrackingTimeMapper userTrackingTimeMapper, UserTrackingTimeSearchRepository userTrackingTimeSearchRepository,
    		UserRepository userRepository, RedisService redisService, UserMapper userMapper) {
        this.userTrackingTimeRepository = userTrackingTimeRepository;
        this.userTrackingTimeMapper = userTrackingTimeMapper;
        this.userTrackingTimeSearchRepository = userTrackingTimeSearchRepository;
        this.userRepository = userRepository;
        this.redisService = redisService;
        this.userMapper = userMapper;
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
    
    /**
     * Write data to userTrackingTime
     * @param userLogin
     * @param isTracking
     */
    public void userTrackingTimeLogin(String userLogin) {
    	RedisUser redisUser = this.redisService.getRedisByUserLogin(userLogin);
    	User user = userRepository.findOneByLogin(userLogin).orElse(null);
    	if (user == null) return;
    	
    	UserTrackingTimeDTO userTrackingTimeDTO = new UserTrackingTimeDTO();
    	
    	userTrackingTimeDTO.setUserId(user.getId());
    	userTrackingTimeDTO.setUserLogin(user.getLogin());
    	userTrackingTimeDTO.setEndTime(Instant.now());
    	
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    	LocalDateTime startTimeFormat = LocalDateTime.parse(redisUser.getStartTime(), formatter);
    	Instant startTime = startTimeFormat.toInstant(ZoneOffset.UTC);
    	
		Duration duration = Duration.between(startTime, Instant.now());
		long minutes = duration.toMinutes();
		if (minutes == 0) minutes = 1;
		
		userTrackingTimeDTO.setDuration(Double.valueOf(minutes));
		userTrackingTimeDTO.setStartTime(startTime);
    	userTrackingTimeDTO.setRole(user.getAuthorities().toString());
    	
    	this.save(userTrackingTimeDTO);
    }
    
    /**
     * Get data dash board for user.
     * @param userId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<Map<String, Object>> dashboardUser(Long userId, Instant startTime, Instant endTime) {
    	User user = userRepository.findById(userId).orElse(null);
    	List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
    	List<Object> listTrackingTime = userTrackingTimeRepository.dashboardUserTracking(userId, startTime, endTime);
    	if (CollectionUtils.isEmpty(listTrackingTime)) return results;
    	
    	results = listTrackingTime.stream().map(tracking -> {
    		Map<String, Object> itemTracking = new HashMap<>();
    		Object[] trackingArr = (Object[])tracking;
    		itemTracking.put("time", trackingArr[0].toString());
    		itemTracking.put("value", trackingArr[1].toString());
    		return itemTracking;
    	}).collect(Collectors.toList());
    	Map<String, Object> userDetail = new HashMap<>();
    	userDetail.put("userDetail", userMapper.userToUserDTO(user));
    	results.add(userDetail);
    	
    	return results;
    }
    
    /**
     * Get dashboard for age.
     * @return
     */
    public List<Map<String, Object>> dashboardUserAge() {
    	List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
    	List<Object> listTrackingAge = userTrackingTimeRepository.dashboardUserTrackingAge();
    	
    	if (CollectionUtils.isEmpty(listTrackingAge)) return results;
    	results = listTrackingAge.stream().map(tracking -> {
    		Map<String, Object> itemTracking = new HashMap<>();
    		Object[] trackingArr = (Object[])tracking;
    		itemTracking.put("age", trackingArr[0].toString());
    		itemTracking.put("countValue", trackingArr[1].toString());
    		return itemTracking;
    	}).collect(Collectors.toList());
    	
    	return results;
    }
}
