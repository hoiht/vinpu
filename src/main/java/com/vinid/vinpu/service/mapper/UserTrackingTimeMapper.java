package com.vinid.vinpu.service.mapper;


import com.vinid.vinpu.domain.*;
import com.vinid.vinpu.service.dto.UserTrackingTimeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserTrackingTime} and its DTO {@link UserTrackingTimeDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface UserTrackingTimeMapper extends EntityMapper<UserTrackingTimeDTO, UserTrackingTime> {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.login", target = "userLogin")
    UserTrackingTimeDTO toDto(UserTrackingTime userTrackingTime);

    @Mapping(source = "userId", target = "user")
    UserTrackingTime toEntity(UserTrackingTimeDTO userTrackingTimeDTO);

    default UserTrackingTime fromId(Long id) {
        if (id == null) {
            return null;
        }
        UserTrackingTime userTrackingTime = new UserTrackingTime();
        userTrackingTime.setId(id);
        return userTrackingTime;
    }
}
