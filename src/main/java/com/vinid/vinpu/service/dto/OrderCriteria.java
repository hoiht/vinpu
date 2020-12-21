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
 * Criteria class for the {@link com.vinid.vinpu.domain.Order} entity. This class is used
 * in {@link com.vinid.vinpu.web.rest.OrderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class OrderCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter from;

    private StringFilter service;

    private InstantFilter time;

    public OrderCriteria() {
    }

    public OrderCriteria(OrderCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.from = other.from == null ? null : other.from.copy();
        this.service = other.service == null ? null : other.service.copy();
        this.time = other.time == null ? null : other.time.copy();
    }

    @Override
    public OrderCriteria copy() {
        return new OrderCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getFrom() {
        return from;
    }

    public void setFrom(IntegerFilter from) {
        this.from = from;
    }

    public StringFilter getService() {
        return service;
    }

    public void setService(StringFilter service) {
        this.service = service;
    }

    public InstantFilter getTime() {
        return time;
    }

    public void setTime(InstantFilter time) {
        this.time = time;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final OrderCriteria that = (OrderCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(from, that.from) &&
            Objects.equals(service, that.service) &&
            Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        from,
        service,
        time
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (from != null ? "from=" + from + ", " : "") +
                (service != null ? "service=" + service + ", " : "") +
                (time != null ? "time=" + time + ", " : "") +
            "}";
    }

}
