/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.daos;


import io.github.ericdriggs.reportcard.gen.db.tables.Context;
import io.github.ericdriggs.reportcard.gen.db.tables.records.ContextRecord;

import java.util.List;

import lombok.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class ContextDao extends DAOImpl<ContextRecord, io.github.ericdriggs.reportcard.gen.db.tables.pojos.Context, Long> {

    /**
     * Create a new ContextDao without any configuration
     */
    public ContextDao() {
        super(Context.CONTEXT, io.github.ericdriggs.reportcard.gen.db.tables.pojos.Context.class);
    }

    /**
     * Create a new ContextDao with an attached configuration
     */
    public ContextDao(Configuration configuration) {
        super(Context.CONTEXT, io.github.ericdriggs.reportcard.gen.db.tables.pojos.Context.class, configuration);
    }

    @Override
    public Long getId(io.github.ericdriggs.reportcard.gen.db.tables.pojos.Context object) {
        return object.getContextId();
    }

    /**
     * Fetch records that have <code>context_id BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Context> fetchRangeOfContextId(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(Context.CONTEXT.CONTEXT_ID, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>context_id IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Context> fetchByContextId(Long... values) {
        return fetch(Context.CONTEXT.CONTEXT_ID, values);
    }

    /**
     * Fetch a unique record that has <code>context_id = value</code>
     */
    public io.github.ericdriggs.reportcard.gen.db.tables.pojos.Context fetchOneByContextId(Long value) {
        return fetchOne(Context.CONTEXT.CONTEXT_ID, value);
    }

    /**
     * Fetch records that have <code>sha_fk BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Context> fetchRangeOfShaFk(Long lowerInclusive, Long upperInclusive) {
        return fetchRange(Context.CONTEXT.SHA_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>sha_fk IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Context> fetchByShaFk(Long... values) {
        return fetch(Context.CONTEXT.SHA_FK, values);
    }

    /**
     * Fetch records that have <code>host BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Context> fetchRangeOfHost(String lowerInclusive, String upperInclusive) {
        return fetchRange(Context.CONTEXT.HOST, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>host IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Context> fetchByHost(String... values) {
        return fetch(Context.CONTEXT.HOST, values);
    }

    /**
     * Fetch records that have <code>branch_fk BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Context> fetchRangeOfBranchFk(Integer lowerInclusive, Integer upperInclusive) {
        return fetchRange(Context.CONTEXT.BRANCH_FK, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>branch_fk IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Context> fetchByBranchFk(Integer... values) {
        return fetch(Context.CONTEXT.BRANCH_FK, values);
    }

    /**
     * Fetch records that have <code>metadata BETWEEN lowerInclusive AND upperInclusive</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Context> fetchRangeOfMetadata(String lowerInclusive, String upperInclusive) {
        return fetchRange(Context.CONTEXT.METADATA, lowerInclusive, upperInclusive);
    }

    /**
     * Fetch records that have <code>metadata IN (values)</code>
     */
    public List<io.github.ericdriggs.reportcard.gen.db.tables.pojos.Context> fetchByMetadata(String... values) {
        return fetch(Context.CONTEXT.METADATA, values);
    }
}
