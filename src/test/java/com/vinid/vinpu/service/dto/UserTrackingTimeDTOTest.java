package com.vinid.vinpu.service.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.vinid.vinpu.web.rest.TestUtil;

public class UserTrackingTimeDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserTrackingTimeDTO.class);
        UserTrackingTimeDTO userTrackingTimeDTO1 = new UserTrackingTimeDTO();
        userTrackingTimeDTO1.setId(1L);
        UserTrackingTimeDTO userTrackingTimeDTO2 = new UserTrackingTimeDTO();
        assertThat(userTrackingTimeDTO1).isNotEqualTo(userTrackingTimeDTO2);
        userTrackingTimeDTO2.setId(userTrackingTimeDTO1.getId());
        assertThat(userTrackingTimeDTO1).isEqualTo(userTrackingTimeDTO2);
        userTrackingTimeDTO2.setId(2L);
        assertThat(userTrackingTimeDTO1).isNotEqualTo(userTrackingTimeDTO2);
        userTrackingTimeDTO1.setId(null);
        assertThat(userTrackingTimeDTO1).isNotEqualTo(userTrackingTimeDTO2);
    }
}
