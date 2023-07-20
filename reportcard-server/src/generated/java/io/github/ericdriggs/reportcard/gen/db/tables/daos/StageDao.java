/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.daos;


import io.github.ericdriggs.reportcard.gen.db.tables.Stage;
import io.github.ericdriggs.reportcard.gen.db.tables.records.StageRecord;

import java.util.List;
import java.util.Optional;

import lombok.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StageDao extends DAOImpl<StageRecord, io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage, Long> {

    /**
     * Create a new StageDao without any configuration
     */
    public StageDao() {
        super(Stage.STAGE, io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage.class);
    }

    /**
     * Create a new StageDao with an attached configuration
     */
    public StageDao(Configuration configuration) {
        super(Stage.STAGE, io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage.class, configuration);
    }

    @Override
    public Long getId(io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage object) {
        return object.getStageId();
    }

    /**
     * Fetch records that have <code>stage_id BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage> fetchRangeOfStageId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(Stage.STAGE.STAGE_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>stage_id IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage> fetchByStageId(Long... values) {
        return fetch(Stage.STAGE.STAGE_ID, values);
    }

    /**
     * Fetch a unique record that has <code>stage_id = value</code>
     */
    public io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage fetchOneByStageId(Long value) {
        return fetchOne(Stage.STAGE.STAGE_ID, value);
    }

    /**
     * Fetch a unique record that has <code>stage_id = value</code>
     */
    public Optional<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage> fetchOptionalByStageId(Long value) {
        return fetchOptional(Stage.STAGE.STAGE_ID, value);
    }

    /**
     * Fetch records that have <code>stage_name BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage> fetchRangeOfStageName(String lowerInclusive, String upperInclusive) {
        return fetchRange(Stage.STAGE.STAGE_NAME, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>stage_name IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage> fetchByStageName(String... values) {
        return fetch(Stage.STAGE.STAGE_NAME, values);
    }

    /**
     * Fetch records that have <code>run_fk BETWEEN lowerInclusive AND
     * upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage> fetchRangeOfRunFk(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(Stage.STAGE.RUN_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>run_fk IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Stage> fetchByRunFk(Long... values) {
        return fetch(Stage.STAGE.RUN_FK, values);
    }
}
