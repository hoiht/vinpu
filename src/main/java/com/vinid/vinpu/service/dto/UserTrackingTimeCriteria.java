package com.vinid.vinpu.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.vinid.vinpu.domain.UserTrackingTime} entity. This class is used
 * in {@link com.vinid.vinpu.web.rest.UserTrackingTimeResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-tracking-times?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class UserTrackingTimeCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private InstantFilter startTime;

    private InstantFilter endTime;

    private DoubleFilter duration;

    private StringFilter role;

    private LongFilter userId;

    public UserTrackingTimeCriteria() {
    }

    public UserTrackingTimeCriteria(UserTrackingTimeCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.startTime = other.startTime == null ? null : other.startTime.copy();
        this.endTime = other.endTime == null ? null : other.endTime.copy();
        this.duration = other.duration == null ? null : other.duration.copy();
        this.role = other.role == null ? null : other.role.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public UserTrackingTimeCriteria copy() {
        return new UserTrackingTimeCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public InstantFilter getStartTime() {
        return startTime;
    }

    public void setStartTime(InstantFilter startTime) {
        this.startTime = startTime;
    }

    public InstantFilter getEndTime() {
        return endTime;
    }

    public void setEndTime(InstantFilter endTime) {
        this.endTime = endTime;
    }

    public DoubleFilter getDuration() {
        return duration;
    }

    public void setDuration(DoubleFilter duration) {
        this.duration = duration;
    }

    public StringFilter getRole() {
        return role;
    }

    public void setRole(StringFilter role) {
        this.role = role;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UserTrackingTimeCriteria that = (UserTrackingTimeCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(startTime, that.startTime) &&
            Objects.equals(endTime, that.endTime) &&
            Objects.equals(duration, that.duration) &&
            Objects.equals(role, that.role) &&
            Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        startTime,
        endTime,
        duration,
        role,
        userId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserTrackingTimeCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (startTime != null ? "startTime=" + startTime + ", " : "") +
                (endTime != null ? "endTime=" + endTime + ", " : "") +
                (duration != null ? "duration=" + duration + ", " : "") +
                (role != null ? "role=" + role + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }

}
