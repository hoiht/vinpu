package com.vinid.vinpu.repository;

import com.vinid.vinpu.domain.UserTrackingTime;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data  repository for the UserTrackingTime entity.
 */
@Repository
public interface UserTrackingTimeRepository extends JpaRepository<UserTrackingTime, Long>, JpaSpecificationExecutor<UserTrackingTime> {

    @Query("select userTrackingTime from UserTrackingTime userTrackingTime where userTrackingTime.user.login = ?#{principal.username}")
    List<UserTrackingTime> findByUserIsCurrentUser();
    
    Optional<UserTrackingTime> findByUserId(Long userId);
    
    @Query("select userTrackingTime from UserTrackingTime userTrackingTime where userTrackingTime.user.id =:userId"
    		+ " and userTrackingTime.startTime >=:startTime"
    		+ " and userTrackingTime.endTime <=:endTime")
    List<UserTrackingTime> getListUserTrackingById(@Param("userId") Long userId, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime);
    
    @Query("select userTrackingTime.endTime, sum(userTrackingTime.duration) as duration from UserTrackingTime userTrackingTime where userTrackingTime.user.id =:userId"
    		+ " and userTrackingTime.startTime >=:startTime"
    		+ " and (:endTime is null or userTrackingTime.endTime <=:endTime)"
    		+ " GROUP BY DATE(userTrackingTime.endTime)")
    List<Object> dashboardUserTracking(@Param("userId") Long userId, @Param("startTime") Instant startTime, @Param("endTime") Instant endTime);
}
