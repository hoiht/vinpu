package com.vinid.vinpu.web.rest;

import com.vinid.vinpu.RedisTestContainerExtension;
import com.vinid.vinpu.VinpuApp;
import com.vinid.vinpu.domain.UserTrackingTime;
import com.vinid.vinpu.domain.User;
import com.vinid.vinpu.repository.UserTrackingTimeRepository;
import com.vinid.vinpu.repository.search.UserTrackingTimeSearchRepository;
import com.vinid.vinpu.service.UserTrackingTimeService;
import com.vinid.vinpu.service.dto.UserTrackingTimeDTO;
import com.vinid.vinpu.service.mapper.UserTrackingTimeMapper;
import com.vinid.vinpu.service.dto.UserTrackingTimeCriteria;
import com.vinid.vinpu.service.UserTrackingTimeQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link UserTrackingTimeResource} REST controller.
 */
@SpringBootTest(classes = VinpuApp.class)
@ExtendWith({ RedisTestContainerExtension.class, MockitoExtension.class })
@AutoConfigureMockMvc
@WithMockUser
public class UserTrackingTimeResourceIT {

    private static final Instant DEFAULT_START_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Double DEFAULT_DURATION = 1D;
    private static final Double UPDATED_DURATION = 2D;
    private static final Double SMALLER_DURATION = 1D - 1D;

    private static final String DEFAULT_ROLE = "AAAAAAAAAA";
    private static final String UPDATED_ROLE = "BBBBBBBBBB";

    @Autowired
    private UserTrackingTimeRepository userTrackingTimeRepository;

    @Autowired
    private UserTrackingTimeMapper userTrackingTimeMapper;

    @Autowired
    private UserTrackingTimeService userTrackingTimeService;

    /**
     * This repository is mocked in the com.vinid.vinpu.repository.search test package.
     *
     * @see com.vinid.vinpu.repository.search.UserTrackingTimeSearchRepositoryMockConfiguration
     */
    @Autowired
    private UserTrackingTimeSearchRepository mockUserTrackingTimeSearchRepository;

    @Autowired
    private UserTrackingTimeQueryService userTrackingTimeQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restUserTrackingTimeMockMvc;

    private UserTrackingTime userTrackingTime;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserTrackingTime createEntity(EntityManager em) {
        UserTrackingTime userTrackingTime = new UserTrackingTime()
            .startTime(DEFAULT_START_TIME)
            .endTime(DEFAULT_END_TIME)
            .duration(DEFAULT_DURATION)
            .role(DEFAULT_ROLE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        userTrackingTime.setUser(user);
        return userTrackingTime;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserTrackingTime createUpdatedEntity(EntityManager em) {
        UserTrackingTime userTrackingTime = new UserTrackingTime()
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .duration(UPDATED_DURATION)
            .role(UPDATED_ROLE);
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        userTrackingTime.setUser(user);
        return userTrackingTime;
    }

    @BeforeEach
    public void initTest() {
        userTrackingTime = createEntity(em);
    }

    @Test
    @Transactional
    public void createUserTrackingTime() throws Exception {
        int databaseSizeBeforeCreate = userTrackingTimeRepository.findAll().size();
        // Create the UserTrackingTime
        UserTrackingTimeDTO userTrackingTimeDTO = userTrackingTimeMapper.toDto(userTrackingTime);
        restUserTrackingTimeMockMvc.perform(post("/api/user-tracking-times")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userTrackingTimeDTO)))
            .andExpect(status().isCreated());

        // Validate the UserTrackingTime in the database
        List<UserTrackingTime> userTrackingTimeList = userTrackingTimeRepository.findAll();
        assertThat(userTrackingTimeList).hasSize(databaseSizeBeforeCreate + 1);
        UserTrackingTime testUserTrackingTime = userTrackingTimeList.get(userTrackingTimeList.size() - 1);
        assertThat(testUserTrackingTime.getStartTime()).isEqualTo(DEFAULT_START_TIME);
        assertThat(testUserTrackingTime.getEndTime()).isEqualTo(DEFAULT_END_TIME);
        assertThat(testUserTrackingTime.getDuration()).isEqualTo(DEFAULT_DURATION);
        assertThat(testUserTrackingTime.getRole()).isEqualTo(DEFAULT_ROLE);

        // Validate the UserTrackingTime in Elasticsearch
        verify(mockUserTrackingTimeSearchRepository, times(1)).save(testUserTrackingTime);
    }

    @Test
    @Transactional
    public void createUserTrackingTimeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = userTrackingTimeRepository.findAll().size();

        // Create the UserTrackingTime with an existing ID
        userTrackingTime.setId(1L);
        UserTrackingTimeDTO userTrackingTimeDTO = userTrackingTimeMapper.toDto(userTrackingTime);

        // An entity with an existing ID cannot be created, so this API call must fail
        restUserTrackingTimeMockMvc.perform(post("/api/user-tracking-times")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userTrackingTimeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserTrackingTime in the database
        List<UserTrackingTime> userTrackingTimeList = userTrackingTimeRepository.findAll();
        assertThat(userTrackingTimeList).hasSize(databaseSizeBeforeCreate);

        // Validate the UserTrackingTime in Elasticsearch
        verify(mockUserTrackingTimeSearchRepository, times(0)).save(userTrackingTime);
    }


    @Test
    @Transactional
    public void checkStartTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = userTrackingTimeRepository.findAll().size();
        // set the field null
        userTrackingTime.setStartTime(null);

        // Create the UserTrackingTime, which fails.
        UserTrackingTimeDTO userTrackingTimeDTO = userTrackingTimeMapper.toDto(userTrackingTime);


        restUserTrackingTimeMockMvc.perform(post("/api/user-tracking-times")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userTrackingTimeDTO)))
            .andExpect(status().isBadRequest());

        List<UserTrackingTime> userTrackingTimeList = userTrackingTimeRepository.findAll();
        assertThat(userTrackingTimeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEndTimeIsRequired() throws Exception {
        int databaseSizeBeforeTest = userTrackingTimeRepository.findAll().size();
        // set the field null
        userTrackingTime.setEndTime(null);

        // Create the UserTrackingTime, which fails.
        UserTrackingTimeDTO userTrackingTimeDTO = userTrackingTimeMapper.toDto(userTrackingTime);


        restUserTrackingTimeMockMvc.perform(post("/api/user-tracking-times")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userTrackingTimeDTO)))
            .andExpect(status().isBadRequest());

        List<UserTrackingTime> userTrackingTimeList = userTrackingTimeRepository.findAll();
        assertThat(userTrackingTimeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimes() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList
        restUserTrackingTimeMockMvc.perform(get("/api/user-tracking-times?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userTrackingTime.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.doubleValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE)));
    }
    
    @Test
    @Transactional
    public void getUserTrackingTime() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get the userTrackingTime
        restUserTrackingTimeMockMvc.perform(get("/api/user-tracking-times/{id}", userTrackingTime.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(userTrackingTime.getId().intValue()))
            .andExpect(jsonPath("$.startTime").value(DEFAULT_START_TIME.toString()))
            .andExpect(jsonPath("$.endTime").value(DEFAULT_END_TIME.toString()))
            .andExpect(jsonPath("$.duration").value(DEFAULT_DURATION.doubleValue()))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE));
    }


    @Test
    @Transactional
    public void getUserTrackingTimesByIdFiltering() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        Long id = userTrackingTime.getId();

        defaultUserTrackingTimeShouldBeFound("id.equals=" + id);
        defaultUserTrackingTimeShouldNotBeFound("id.notEquals=" + id);

        defaultUserTrackingTimeShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultUserTrackingTimeShouldNotBeFound("id.greaterThan=" + id);

        defaultUserTrackingTimeShouldBeFound("id.lessThanOrEqual=" + id);
        defaultUserTrackingTimeShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllUserTrackingTimesByStartTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where startTime equals to DEFAULT_START_TIME
        defaultUserTrackingTimeShouldBeFound("startTime.equals=" + DEFAULT_START_TIME);

        // Get all the userTrackingTimeList where startTime equals to UPDATED_START_TIME
        defaultUserTrackingTimeShouldNotBeFound("startTime.equals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByStartTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where startTime not equals to DEFAULT_START_TIME
        defaultUserTrackingTimeShouldNotBeFound("startTime.notEquals=" + DEFAULT_START_TIME);

        // Get all the userTrackingTimeList where startTime not equals to UPDATED_START_TIME
        defaultUserTrackingTimeShouldBeFound("startTime.notEquals=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByStartTimeIsInShouldWork() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where startTime in DEFAULT_START_TIME or UPDATED_START_TIME
        defaultUserTrackingTimeShouldBeFound("startTime.in=" + DEFAULT_START_TIME + "," + UPDATED_START_TIME);

        // Get all the userTrackingTimeList where startTime equals to UPDATED_START_TIME
        defaultUserTrackingTimeShouldNotBeFound("startTime.in=" + UPDATED_START_TIME);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByStartTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where startTime is not null
        defaultUserTrackingTimeShouldBeFound("startTime.specified=true");

        // Get all the userTrackingTimeList where startTime is null
        defaultUserTrackingTimeShouldNotBeFound("startTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByEndTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where endTime equals to DEFAULT_END_TIME
        defaultUserTrackingTimeShouldBeFound("endTime.equals=" + DEFAULT_END_TIME);

        // Get all the userTrackingTimeList where endTime equals to UPDATED_END_TIME
        defaultUserTrackingTimeShouldNotBeFound("endTime.equals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByEndTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where endTime not equals to DEFAULT_END_TIME
        defaultUserTrackingTimeShouldNotBeFound("endTime.notEquals=" + DEFAULT_END_TIME);

        // Get all the userTrackingTimeList where endTime not equals to UPDATED_END_TIME
        defaultUserTrackingTimeShouldBeFound("endTime.notEquals=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByEndTimeIsInShouldWork() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where endTime in DEFAULT_END_TIME or UPDATED_END_TIME
        defaultUserTrackingTimeShouldBeFound("endTime.in=" + DEFAULT_END_TIME + "," + UPDATED_END_TIME);

        // Get all the userTrackingTimeList where endTime equals to UPDATED_END_TIME
        defaultUserTrackingTimeShouldNotBeFound("endTime.in=" + UPDATED_END_TIME);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByEndTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where endTime is not null
        defaultUserTrackingTimeShouldBeFound("endTime.specified=true");

        // Get all the userTrackingTimeList where endTime is null
        defaultUserTrackingTimeShouldNotBeFound("endTime.specified=false");
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByDurationIsEqualToSomething() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where duration equals to DEFAULT_DURATION
        defaultUserTrackingTimeShouldBeFound("duration.equals=" + DEFAULT_DURATION);

        // Get all the userTrackingTimeList where duration equals to UPDATED_DURATION
        defaultUserTrackingTimeShouldNotBeFound("duration.equals=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByDurationIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where duration not equals to DEFAULT_DURATION
        defaultUserTrackingTimeShouldNotBeFound("duration.notEquals=" + DEFAULT_DURATION);

        // Get all the userTrackingTimeList where duration not equals to UPDATED_DURATION
        defaultUserTrackingTimeShouldBeFound("duration.notEquals=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByDurationIsInShouldWork() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where duration in DEFAULT_DURATION or UPDATED_DURATION
        defaultUserTrackingTimeShouldBeFound("duration.in=" + DEFAULT_DURATION + "," + UPDATED_DURATION);

        // Get all the userTrackingTimeList where duration equals to UPDATED_DURATION
        defaultUserTrackingTimeShouldNotBeFound("duration.in=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByDurationIsNullOrNotNull() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where duration is not null
        defaultUserTrackingTimeShouldBeFound("duration.specified=true");

        // Get all the userTrackingTimeList where duration is null
        defaultUserTrackingTimeShouldNotBeFound("duration.specified=false");
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByDurationIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where duration is greater than or equal to DEFAULT_DURATION
        defaultUserTrackingTimeShouldBeFound("duration.greaterThanOrEqual=" + DEFAULT_DURATION);

        // Get all the userTrackingTimeList where duration is greater than or equal to UPDATED_DURATION
        defaultUserTrackingTimeShouldNotBeFound("duration.greaterThanOrEqual=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByDurationIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where duration is less than or equal to DEFAULT_DURATION
        defaultUserTrackingTimeShouldBeFound("duration.lessThanOrEqual=" + DEFAULT_DURATION);

        // Get all the userTrackingTimeList where duration is less than or equal to SMALLER_DURATION
        defaultUserTrackingTimeShouldNotBeFound("duration.lessThanOrEqual=" + SMALLER_DURATION);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByDurationIsLessThanSomething() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where duration is less than DEFAULT_DURATION
        defaultUserTrackingTimeShouldNotBeFound("duration.lessThan=" + DEFAULT_DURATION);

        // Get all the userTrackingTimeList where duration is less than UPDATED_DURATION
        defaultUserTrackingTimeShouldBeFound("duration.lessThan=" + UPDATED_DURATION);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByDurationIsGreaterThanSomething() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where duration is greater than DEFAULT_DURATION
        defaultUserTrackingTimeShouldNotBeFound("duration.greaterThan=" + DEFAULT_DURATION);

        // Get all the userTrackingTimeList where duration is greater than SMALLER_DURATION
        defaultUserTrackingTimeShouldBeFound("duration.greaterThan=" + SMALLER_DURATION);
    }


    @Test
    @Transactional
    public void getAllUserTrackingTimesByRoleIsEqualToSomething() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where role equals to DEFAULT_ROLE
        defaultUserTrackingTimeShouldBeFound("role.equals=" + DEFAULT_ROLE);

        // Get all the userTrackingTimeList where role equals to UPDATED_ROLE
        defaultUserTrackingTimeShouldNotBeFound("role.equals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByRoleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where role not equals to DEFAULT_ROLE
        defaultUserTrackingTimeShouldNotBeFound("role.notEquals=" + DEFAULT_ROLE);

        // Get all the userTrackingTimeList where role not equals to UPDATED_ROLE
        defaultUserTrackingTimeShouldBeFound("role.notEquals=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByRoleIsInShouldWork() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where role in DEFAULT_ROLE or UPDATED_ROLE
        defaultUserTrackingTimeShouldBeFound("role.in=" + DEFAULT_ROLE + "," + UPDATED_ROLE);

        // Get all the userTrackingTimeList where role equals to UPDATED_ROLE
        defaultUserTrackingTimeShouldNotBeFound("role.in=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByRoleIsNullOrNotNull() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where role is not null
        defaultUserTrackingTimeShouldBeFound("role.specified=true");

        // Get all the userTrackingTimeList where role is null
        defaultUserTrackingTimeShouldNotBeFound("role.specified=false");
    }
                @Test
    @Transactional
    public void getAllUserTrackingTimesByRoleContainsSomething() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where role contains DEFAULT_ROLE
        defaultUserTrackingTimeShouldBeFound("role.contains=" + DEFAULT_ROLE);

        // Get all the userTrackingTimeList where role contains UPDATED_ROLE
        defaultUserTrackingTimeShouldNotBeFound("role.contains=" + UPDATED_ROLE);
    }

    @Test
    @Transactional
    public void getAllUserTrackingTimesByRoleNotContainsSomething() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        // Get all the userTrackingTimeList where role does not contain DEFAULT_ROLE
        defaultUserTrackingTimeShouldNotBeFound("role.doesNotContain=" + DEFAULT_ROLE);

        // Get all the userTrackingTimeList where role does not contain UPDATED_ROLE
        defaultUserTrackingTimeShouldBeFound("role.doesNotContain=" + UPDATED_ROLE);
    }


    @Test
    @Transactional
    public void getAllUserTrackingTimesByUserIsEqualToSomething() throws Exception {
        // Get already existing entity
        User user = userTrackingTime.getUser();
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);
        Long userId = user.getId();

        // Get all the userTrackingTimeList where user equals to userId
        defaultUserTrackingTimeShouldBeFound("userId.equals=" + userId);

        // Get all the userTrackingTimeList where user equals to userId + 1
        defaultUserTrackingTimeShouldNotBeFound("userId.equals=" + (userId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserTrackingTimeShouldBeFound(String filter) throws Exception {
        restUserTrackingTimeMockMvc.perform(get("/api/user-tracking-times?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userTrackingTime.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.doubleValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE)));

        // Check, that the count call also returns 1
        restUserTrackingTimeMockMvc.perform(get("/api/user-tracking-times/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultUserTrackingTimeShouldNotBeFound(String filter) throws Exception {
        restUserTrackingTimeMockMvc.perform(get("/api/user-tracking-times?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restUserTrackingTimeMockMvc.perform(get("/api/user-tracking-times/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingUserTrackingTime() throws Exception {
        // Get the userTrackingTime
        restUserTrackingTimeMockMvc.perform(get("/api/user-tracking-times/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUserTrackingTime() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        int databaseSizeBeforeUpdate = userTrackingTimeRepository.findAll().size();

        // Update the userTrackingTime
        UserTrackingTime updatedUserTrackingTime = userTrackingTimeRepository.findById(userTrackingTime.getId()).get();
        // Disconnect from session so that the updates on updatedUserTrackingTime are not directly saved in db
        em.detach(updatedUserTrackingTime);
        updatedUserTrackingTime
            .startTime(UPDATED_START_TIME)
            .endTime(UPDATED_END_TIME)
            .duration(UPDATED_DURATION)
            .role(UPDATED_ROLE);
        UserTrackingTimeDTO userTrackingTimeDTO = userTrackingTimeMapper.toDto(updatedUserTrackingTime);

        restUserTrackingTimeMockMvc.perform(put("/api/user-tracking-times")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userTrackingTimeDTO)))
            .andExpect(status().isOk());

        // Validate the UserTrackingTime in the database
        List<UserTrackingTime> userTrackingTimeList = userTrackingTimeRepository.findAll();
        assertThat(userTrackingTimeList).hasSize(databaseSizeBeforeUpdate);
        UserTrackingTime testUserTrackingTime = userTrackingTimeList.get(userTrackingTimeList.size() - 1);
        assertThat(testUserTrackingTime.getStartTime()).isEqualTo(UPDATED_START_TIME);
        assertThat(testUserTrackingTime.getEndTime()).isEqualTo(UPDATED_END_TIME);
        assertThat(testUserTrackingTime.getDuration()).isEqualTo(UPDATED_DURATION);
        assertThat(testUserTrackingTime.getRole()).isEqualTo(UPDATED_ROLE);

        // Validate the UserTrackingTime in Elasticsearch
        verify(mockUserTrackingTimeSearchRepository, times(1)).save(testUserTrackingTime);
    }

    @Test
    @Transactional
    public void updateNonExistingUserTrackingTime() throws Exception {
        int databaseSizeBeforeUpdate = userTrackingTimeRepository.findAll().size();

        // Create the UserTrackingTime
        UserTrackingTimeDTO userTrackingTimeDTO = userTrackingTimeMapper.toDto(userTrackingTime);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restUserTrackingTimeMockMvc.perform(put("/api/user-tracking-times")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(userTrackingTimeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the UserTrackingTime in the database
        List<UserTrackingTime> userTrackingTimeList = userTrackingTimeRepository.findAll();
        assertThat(userTrackingTimeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the UserTrackingTime in Elasticsearch
        verify(mockUserTrackingTimeSearchRepository, times(0)).save(userTrackingTime);
    }

    @Test
    @Transactional
    public void deleteUserTrackingTime() throws Exception {
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);

        int databaseSizeBeforeDelete = userTrackingTimeRepository.findAll().size();

        // Delete the userTrackingTime
        restUserTrackingTimeMockMvc.perform(delete("/api/user-tracking-times/{id}", userTrackingTime.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<UserTrackingTime> userTrackingTimeList = userTrackingTimeRepository.findAll();
        assertThat(userTrackingTimeList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the UserTrackingTime in Elasticsearch
        verify(mockUserTrackingTimeSearchRepository, times(1)).deleteById(userTrackingTime.getId());
    }

    @Test
    @Transactional
    public void searchUserTrackingTime() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        userTrackingTimeRepository.saveAndFlush(userTrackingTime);
        when(mockUserTrackingTimeSearchRepository.search(queryStringQuery("id:" + userTrackingTime.getId())))
            .thenReturn(Collections.singletonList(userTrackingTime));

        // Search the userTrackingTime
        restUserTrackingTimeMockMvc.perform(get("/api/_search/user-tracking-times?query=id:" + userTrackingTime.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(userTrackingTime.getId().intValue())))
            .andExpect(jsonPath("$.[*].startTime").value(hasItem(DEFAULT_START_TIME.toString())))
            .andExpect(jsonPath("$.[*].endTime").value(hasItem(DEFAULT_END_TIME.toString())))
            .andExpect(jsonPath("$.[*].duration").value(hasItem(DEFAULT_DURATION.doubleValue())))
            .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE)));
    }
}
