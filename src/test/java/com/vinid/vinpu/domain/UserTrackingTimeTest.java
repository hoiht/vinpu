package com.vinid.vinpu.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.vinid.vinpu.web.rest.TestUtil;

public class UserTrackingTimeTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserTrackingTime.class);
        UserTrackingTime userTrackingTime1 = new UserTrackingTime();
        userTrackingTime1.setId(1L);
        UserTrackingTime userTrackingTime2 = new UserTrackingTime();
        userTrackingTime2.setId(userTrackingTime1.getId());
        assertThat(userTrackingTime1).isEqualTo(userTrackingTime2);
        userTrackingTime2.setId(2L);
        assertThat(userTrackingTime1).isNotEqualTo(userTrackingTime2);
        userTrackingTime1.setId(null);
        assertThat(userTrackingTime1).isNotEqualTo(userTrackingTime2);
    }
}
