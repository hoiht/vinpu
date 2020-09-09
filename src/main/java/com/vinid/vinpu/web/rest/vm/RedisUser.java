package com.vinid.vinpu.web.rest.vm;

import java.time.Instant;

import javax.validation.constraints.NotNull;

import org.springframework.data.redis.core.RedisHash;

@RedisHash("RedisUser")
public class RedisUser {
    private Long id;
	
	@NotNull
    private String startTime;

    private String endTime;

    private Double duration;

    private String role;
    
    private Long userId;

    private String userLogin;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public Double getDuration() {
		return duration;
	}

	public void setDuration(Double duration) {
		this.duration = duration;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
    
}
