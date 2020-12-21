package com.vinid.vinpu.web.rest;

import com.vinid.vinpu.RedisTestContainerExtension;
import com.vinid.vinpu.VinpuApp;
import com.vinid.vinpu.domain.Order;
import com.vinid.vinpu.repository.OrderRepository;
import com.vinid.vinpu.repository.search.OrderSearchRepository;
import com.vinid.vinpu.service.OrderService;
import com.vinid.vinpu.service.dto.OrderDTO;
import com.vinid.vinpu.service.mapper.OrderMapper;
import com.vinid.vinpu.service.dto.OrderCriteria;
import com.vinid.vinpu.service.OrderQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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
 * Integration tests for the {@link OrderResource} REST controller.
 */
@SpringBootTest(classes = VinpuApp.class)
@ExtendWith({ RedisTestContainerExtension.class, MockitoExtension.class })
@AutoConfigureMockMvc
@WithMockUser
public class OrderResourceIT {

    private static final Integer DEFAULT_FROM = 1;
    private static final Integer UPDATED_FROM = 2;
    private static final Integer SMALLER_FROM = 1 - 1;

    private static final String DEFAULT_SERVICE = "AAAAAAAAAA";
    private static final String UPDATED_SERVICE = "BBBBBBBBBB";

    private static final Instant DEFAULT_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderService orderService;

    /**
     * This repository is mocked in the com.vinid.vinpu.repository.search test package.
     *
     * @see com.vinid.vinpu.repository.search.OrderSearchRepositoryMockConfiguration
     */
    @Autowired
    private OrderSearchRepository mockOrderSearchRepository;

    @Autowired
    private OrderQueryService orderQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restOrderMockMvc;

    private Order order;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createEntity(EntityManager em) {
        Order order = new Order()
            .from(DEFAULT_FROM)
            .service(DEFAULT_SERVICE)
            .time(DEFAULT_TIME);
        return order;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Order createUpdatedEntity(EntityManager em) {
        Order order = new Order()
            .from(UPDATED_FROM)
            .service(UPDATED_SERVICE)
            .time(UPDATED_TIME);
        return order;
    }

    @BeforeEach
    public void initTest() {
        order = createEntity(em);
    }

    @Test
    @Transactional
    public void createOrder() throws Exception {
        int databaseSizeBeforeCreate = orderRepository.findAll().size();
        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);
        restOrderMockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isCreated());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate + 1);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getFrom()).isEqualTo(DEFAULT_FROM);
        assertThat(testOrder.getService()).isEqualTo(DEFAULT_SERVICE);
        assertThat(testOrder.getTime()).isEqualTo(DEFAULT_TIME);

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(1)).save(testOrder);
    }

    @Test
    @Transactional
    public void createOrderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = orderRepository.findAll().size();

        // Create the Order with an existing ID
        order.setId(1L);
        OrderDTO orderDTO = orderMapper.toDto(order);

        // An entity with an existing ID cannot be created, so this API call must fail
        restOrderMockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeCreate);

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(0)).save(order);
    }


    @Test
    @Transactional
    public void checkFromIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderRepository.findAll().size();
        // set the field null
        order.setFrom(null);

        // Create the Order, which fails.
        OrderDTO orderDTO = orderMapper.toDto(order);


        restOrderMockMvc.perform(post("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllOrders() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList
        restOrderMockMvc.perform(get("/api/orders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].from").value(hasItem(DEFAULT_FROM)))
            .andExpect(jsonPath("$.[*].service").value(hasItem(DEFAULT_SERVICE)))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME.toString())));
    }
    
    @Test
    @Transactional
    public void getOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get the order
        restOrderMockMvc.perform(get("/api/orders/{id}", order.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(order.getId().intValue()))
            .andExpect(jsonPath("$.from").value(DEFAULT_FROM))
            .andExpect(jsonPath("$.service").value(DEFAULT_SERVICE))
            .andExpect(jsonPath("$.time").value(DEFAULT_TIME.toString()));
    }


    @Test
    @Transactional
    public void getOrdersByIdFiltering() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        Long id = order.getId();

        defaultOrderShouldBeFound("id.equals=" + id);
        defaultOrderShouldNotBeFound("id.notEquals=" + id);

        defaultOrderShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultOrderShouldNotBeFound("id.greaterThan=" + id);

        defaultOrderShouldBeFound("id.lessThanOrEqual=" + id);
        defaultOrderShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllOrdersByFromIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where from equals to DEFAULT_FROM
        defaultOrderShouldBeFound("from.equals=" + DEFAULT_FROM);

        // Get all the orderList where from equals to UPDATED_FROM
        defaultOrderShouldNotBeFound("from.equals=" + UPDATED_FROM);
    }

    @Test
    @Transactional
    public void getAllOrdersByFromIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where from not equals to DEFAULT_FROM
        defaultOrderShouldNotBeFound("from.notEquals=" + DEFAULT_FROM);

        // Get all the orderList where from not equals to UPDATED_FROM
        defaultOrderShouldBeFound("from.notEquals=" + UPDATED_FROM);
    }

    @Test
    @Transactional
    public void getAllOrdersByFromIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where from in DEFAULT_FROM or UPDATED_FROM
        defaultOrderShouldBeFound("from.in=" + DEFAULT_FROM + "," + UPDATED_FROM);

        // Get all the orderList where from equals to UPDATED_FROM
        defaultOrderShouldNotBeFound("from.in=" + UPDATED_FROM);
    }

    @Test
    @Transactional
    public void getAllOrdersByFromIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where from is not null
        defaultOrderShouldBeFound("from.specified=true");

        // Get all the orderList where from is null
        defaultOrderShouldNotBeFound("from.specified=false");
    }

    @Test
    @Transactional
    public void getAllOrdersByFromIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where from is greater than or equal to DEFAULT_FROM
        defaultOrderShouldBeFound("from.greaterThanOrEqual=" + DEFAULT_FROM);

        // Get all the orderList where from is greater than or equal to UPDATED_FROM
        defaultOrderShouldNotBeFound("from.greaterThanOrEqual=" + UPDATED_FROM);
    }

    @Test
    @Transactional
    public void getAllOrdersByFromIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where from is less than or equal to DEFAULT_FROM
        defaultOrderShouldBeFound("from.lessThanOrEqual=" + DEFAULT_FROM);

        // Get all the orderList where from is less than or equal to SMALLER_FROM
        defaultOrderShouldNotBeFound("from.lessThanOrEqual=" + SMALLER_FROM);
    }

    @Test
    @Transactional
    public void getAllOrdersByFromIsLessThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where from is less than DEFAULT_FROM
        defaultOrderShouldNotBeFound("from.lessThan=" + DEFAULT_FROM);

        // Get all the orderList where from is less than UPDATED_FROM
        defaultOrderShouldBeFound("from.lessThan=" + UPDATED_FROM);
    }

    @Test
    @Transactional
    public void getAllOrdersByFromIsGreaterThanSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where from is greater than DEFAULT_FROM
        defaultOrderShouldNotBeFound("from.greaterThan=" + DEFAULT_FROM);

        // Get all the orderList where from is greater than SMALLER_FROM
        defaultOrderShouldBeFound("from.greaterThan=" + SMALLER_FROM);
    }


    @Test
    @Transactional
    public void getAllOrdersByServiceIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where service equals to DEFAULT_SERVICE
        defaultOrderShouldBeFound("service.equals=" + DEFAULT_SERVICE);

        // Get all the orderList where service equals to UPDATED_SERVICE
        defaultOrderShouldNotBeFound("service.equals=" + UPDATED_SERVICE);
    }

    @Test
    @Transactional
    public void getAllOrdersByServiceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where service not equals to DEFAULT_SERVICE
        defaultOrderShouldNotBeFound("service.notEquals=" + DEFAULT_SERVICE);

        // Get all the orderList where service not equals to UPDATED_SERVICE
        defaultOrderShouldBeFound("service.notEquals=" + UPDATED_SERVICE);
    }

    @Test
    @Transactional
    public void getAllOrdersByServiceIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where service in DEFAULT_SERVICE or UPDATED_SERVICE
        defaultOrderShouldBeFound("service.in=" + DEFAULT_SERVICE + "," + UPDATED_SERVICE);

        // Get all the orderList where service equals to UPDATED_SERVICE
        defaultOrderShouldNotBeFound("service.in=" + UPDATED_SERVICE);
    }

    @Test
    @Transactional
    public void getAllOrdersByServiceIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where service is not null
        defaultOrderShouldBeFound("service.specified=true");

        // Get all the orderList where service is null
        defaultOrderShouldNotBeFound("service.specified=false");
    }
                @Test
    @Transactional
    public void getAllOrdersByServiceContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where service contains DEFAULT_SERVICE
        defaultOrderShouldBeFound("service.contains=" + DEFAULT_SERVICE);

        // Get all the orderList where service contains UPDATED_SERVICE
        defaultOrderShouldNotBeFound("service.contains=" + UPDATED_SERVICE);
    }

    @Test
    @Transactional
    public void getAllOrdersByServiceNotContainsSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where service does not contain DEFAULT_SERVICE
        defaultOrderShouldNotBeFound("service.doesNotContain=" + DEFAULT_SERVICE);

        // Get all the orderList where service does not contain UPDATED_SERVICE
        defaultOrderShouldBeFound("service.doesNotContain=" + UPDATED_SERVICE);
    }


    @Test
    @Transactional
    public void getAllOrdersByTimeIsEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where time equals to DEFAULT_TIME
        defaultOrderShouldBeFound("time.equals=" + DEFAULT_TIME);

        // Get all the orderList where time equals to UPDATED_TIME
        defaultOrderShouldNotBeFound("time.equals=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllOrdersByTimeIsNotEqualToSomething() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where time not equals to DEFAULT_TIME
        defaultOrderShouldNotBeFound("time.notEquals=" + DEFAULT_TIME);

        // Get all the orderList where time not equals to UPDATED_TIME
        defaultOrderShouldBeFound("time.notEquals=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllOrdersByTimeIsInShouldWork() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where time in DEFAULT_TIME or UPDATED_TIME
        defaultOrderShouldBeFound("time.in=" + DEFAULT_TIME + "," + UPDATED_TIME);

        // Get all the orderList where time equals to UPDATED_TIME
        defaultOrderShouldNotBeFound("time.in=" + UPDATED_TIME);
    }

    @Test
    @Transactional
    public void getAllOrdersByTimeIsNullOrNotNull() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        // Get all the orderList where time is not null
        defaultOrderShouldBeFound("time.specified=true");

        // Get all the orderList where time is null
        defaultOrderShouldNotBeFound("time.specified=false");
    }
    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultOrderShouldBeFound(String filter) throws Exception {
        restOrderMockMvc.perform(get("/api/orders?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].from").value(hasItem(DEFAULT_FROM)))
            .andExpect(jsonPath("$.[*].service").value(hasItem(DEFAULT_SERVICE)))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME.toString())));

        // Check, that the count call also returns 1
        restOrderMockMvc.perform(get("/api/orders/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultOrderShouldNotBeFound(String filter) throws Exception {
        restOrderMockMvc.perform(get("/api/orders?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restOrderMockMvc.perform(get("/api/orders/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingOrder() throws Exception {
        // Get the order
        restOrderMockMvc.perform(get("/api/orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Update the order
        Order updatedOrder = orderRepository.findById(order.getId()).get();
        // Disconnect from session so that the updates on updatedOrder are not directly saved in db
        em.detach(updatedOrder);
        updatedOrder
            .from(UPDATED_FROM)
            .service(UPDATED_SERVICE)
            .time(UPDATED_TIME);
        OrderDTO orderDTO = orderMapper.toDto(updatedOrder);

        restOrderMockMvc.perform(put("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isOk());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);
        Order testOrder = orderList.get(orderList.size() - 1);
        assertThat(testOrder.getFrom()).isEqualTo(UPDATED_FROM);
        assertThat(testOrder.getService()).isEqualTo(UPDATED_SERVICE);
        assertThat(testOrder.getTime()).isEqualTo(UPDATED_TIME);

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(1)).save(testOrder);
    }

    @Test
    @Transactional
    public void updateNonExistingOrder() throws Exception {
        int databaseSizeBeforeUpdate = orderRepository.findAll().size();

        // Create the Order
        OrderDTO orderDTO = orderMapper.toDto(order);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restOrderMockMvc.perform(put("/api/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(orderDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Order in the database
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(0)).save(order);
    }

    @Test
    @Transactional
    public void deleteOrder() throws Exception {
        // Initialize the database
        orderRepository.saveAndFlush(order);

        int databaseSizeBeforeDelete = orderRepository.findAll().size();

        // Delete the order
        restOrderMockMvc.perform(delete("/api/orders/{id}", order.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Order in Elasticsearch
        verify(mockOrderSearchRepository, times(1)).deleteById(order.getId());
    }

    @Test
    @Transactional
    public void searchOrder() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        orderRepository.saveAndFlush(order);
        when(mockOrderSearchRepository.search(queryStringQuery("id:" + order.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(order), PageRequest.of(0, 1), 1));

        // Search the order
        restOrderMockMvc.perform(get("/api/_search/orders?query=id:" + order.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(order.getId().intValue())))
            .andExpect(jsonPath("$.[*].from").value(hasItem(DEFAULT_FROM)))
            .andExpect(jsonPath("$.[*].service").value(hasItem(DEFAULT_SERVICE)))
            .andExpect(jsonPath("$.[*].time").value(hasItem(DEFAULT_TIME.toString())));
    }
}
