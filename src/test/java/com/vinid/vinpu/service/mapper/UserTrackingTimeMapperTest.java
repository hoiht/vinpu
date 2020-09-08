package com.vinid.vinpu.service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class UserTrackingTimeMapperTest {

    private UserTrackingTimeMapper userTrackingTimeMapper;

    @BeforeEach
    public void setUp() {
        userTrackingTimeMapper = new UserTrackingTimeMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(userTrackingTimeMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(userTrackingTimeMapper.fromId(null)).isNull();
    }
}
