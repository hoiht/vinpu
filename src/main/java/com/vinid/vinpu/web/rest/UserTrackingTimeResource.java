package com.vinid.vinpu.web.rest;

import com.vinid.vinpu.service.UserTrackingTimeService;
import com.vinid.vinpu.web.rest.errors.BadRequestAlertException;
import com.vinid.vinpu.service.dto.UserTrackingTimeDTO;
import com.vinid.vinpu.service.dto.UserTrackingTimeCriteria;
import com.vinid.vinpu.service.UserTrackingTimeQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing {@link com.vinid.vinpu.domain.UserTrackingTime}.
 */
@RestController
@RequestMapping("/api")
public class UserTrackingTimeResource {

    private final Logger log = LoggerFactory.getLogger(UserTrackingTimeResource.class);

    private static final String ENTITY_NAME = "userTrackingTime";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UserTrackingTimeService userTrackingTimeService;

    private final UserTrackingTimeQueryService userTrackingTimeQueryService;

    public UserTrackingTimeResource(UserTrackingTimeService userTrackingTimeService, UserTrackingTimeQueryService userTrackingTimeQueryService) {
        this.userTrackingTimeService = userTrackingTimeService;
        this.userTrackingTimeQueryService = userTrackingTimeQueryService;
    }

    /**
     * {@code POST  /user-tracking-times} : Create a new userTrackingTime.
     *
     * @param userTrackingTimeDTO the userTrackingTimeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new userTrackingTimeDTO, or with status {@code 400 (Bad Request)} if the userTrackingTime has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/user-tracking-times")
    public ResponseEntity<UserTrackingTimeDTO> createUserTrackingTime(@Valid @RequestBody UserTrackingTimeDTO userTrackingTimeDTO) throws URISyntaxException {
        log.debug("REST request to save UserTrackingTime : {}", userTrackingTimeDTO);
        if (userTrackingTimeDTO.getId() != null) {
            throw new BadRequestAlertException("A new userTrackingTime cannot already have an ID", ENTITY_NAME, "idexists");
        }
        UserTrackingTimeDTO result = userTrackingTimeService.save(userTrackingTimeDTO);
        return ResponseEntity.created(new URI("/api/user-tracking-times/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /user-tracking-times} : Updates an existing userTrackingTime.
     *
     * @param userTrackingTimeDTO the userTrackingTimeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated userTrackingTimeDTO,
     * or with status {@code 400 (Bad Request)} if the userTrackingTimeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the userTrackingTimeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/user-tracking-times")
    public ResponseEntity<UserTrackingTimeDTO> updateUserTrackingTime(@Valid @RequestBody UserTrackingTimeDTO userTrackingTimeDTO) throws URISyntaxException {
        log.debug("REST request to update UserTrackingTime : {}", userTrackingTimeDTO);
        if (userTrackingTimeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        UserTrackingTimeDTO result = userTrackingTimeService.save(userTrackingTimeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, userTrackingTimeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /user-tracking-times} : get all the userTrackingTimes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of userTrackingTimes in body.
     */
    @GetMapping("/user-tracking-times")
    public ResponseEntity<List<UserTrackingTimeDTO>> getAllUserTrackingTimes(UserTrackingTimeCriteria criteria) {
        log.debug("REST request to get UserTrackingTimes by criteria: {}", criteria);
        List<UserTrackingTimeDTO> entityList = userTrackingTimeQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * {@code GET  /user-tracking-times/count} : count all the userTrackingTimes.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/user-tracking-times/count")
    public ResponseEntity<Long> countUserTrackingTimes(UserTrackingTimeCriteria criteria) {
        log.debug("REST request to count UserTrackingTimes by criteria: {}", criteria);
        return ResponseEntity.ok().body(userTrackingTimeQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /user-tracking-times/:id} : get the "id" userTrackingTime.
     *
     * @param id the id of the userTrackingTimeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the userTrackingTimeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/user-tracking-times/{id}")
    public ResponseEntity<UserTrackingTimeDTO> getUserTrackingTime(@PathVariable Long id) {
        log.debug("REST request to get UserTrackingTime : {}", id);
        Optional<UserTrackingTimeDTO> userTrackingTimeDTO = userTrackingTimeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(userTrackingTimeDTO);
    }

    /**
     * {@code DELETE  /user-tracking-times/:id} : delete the "id" userTrackingTime.
     *
     * @param id the id of the userTrackingTimeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/user-tracking-times/{id}")
    public ResponseEntity<Void> deleteUserTrackingTime(@PathVariable Long id) {
        log.debug("REST request to delete UserTrackingTime : {}", id);
        userTrackingTimeService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/user-tracking-times?query=:query} : search for the userTrackingTime corresponding
     * to the query.
     *
     * @param query the query of the userTrackingTime search.
     * @return the result of the search.
     */
    @GetMapping("/_search/user-tracking-times")
    public List<UserTrackingTimeDTO> searchUserTrackingTimes(@RequestParam String query) {
        log.debug("REST request to search UserTrackingTimes for query {}", query);
        return userTrackingTimeService.search(query);
    }
}
