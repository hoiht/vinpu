package com.vinid.vinpu.repository;

import com.vinid.vinpu.domain.UserTrackingTime;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data  repository for the UserTrackingTime entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserTrackingTimeRepository extends JpaRepository<UserTrackingTime, Long>, JpaSpecificationExecutor<UserTrackingTime> {

    @Query("select userTrackingTime from UserTrackingTime userTrackingTime where userTrackingTime.user.login = ?#{principal.username}")
    List<UserTrackingTime> findByUserIsCurrentUser();
}
