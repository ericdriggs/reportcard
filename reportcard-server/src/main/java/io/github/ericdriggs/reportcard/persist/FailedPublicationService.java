package io.github.ericdriggs.reportcard.persist;

import io.github.ericdriggs.reportcard.controller.browse.BrowseHtmlHelper;
import io.github.ericdriggs.reportcard.gen.db.tables.pojos.StoragePojo;
import io.github.ericdriggs.reportcard.model.publication.FailedPublication;
import io.github.ericdriggs.reportcard.model.publication.FailedPublicationStorage;
import io.github.ericdriggs.reportcard.model.publication.FailedPublicationsResponse;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.ResultQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;
import static org.jooq.impl.DSL.selectOne;

/**
 * Reports stages that have storage but no test result: suspected failed publications.
 */
@Service
public class FailedPublicationService extends AbstractPersistService {

    public static final int DEFAULT_DAYS = 1;
    public static final int DEFAULT_OLDER_THAN_MINUTES = 10;
    public static final int DEFAULT_LIMIT = 50;
    static final int MAX_DAYS = 30;
    static final int MAX_LIMIT = 200;

    @Autowired
    public FailedPublicationService(DSLContext dsl) {
        super(dsl);
    }

    public FailedPublicationsResponse getFailedPublications(Integer days, Integer olderThanMinutes, Integer limit, Instant now) {
        final int validDays = days == null ? DEFAULT_DAYS : days;
        final int validOlderThanMinutes = olderThanMinutes == null ? DEFAULT_OLDER_THAN_MINUTES : olderThanMinutes;
        final int validLimit = limit == null ? DEFAULT_LIMIT : limit;
        if (validDays < 1 || validDays > MAX_DAYS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "days must be between 1 and " + MAX_DAYS);
        }
        if (validOlderThanMinutes < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "olderThanMinutes must be at least 1");
        }
        if (validLimit < 1 || validLimit > MAX_LIMIT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be between 1 and " + MAX_LIMIT);
        }

        final Instant dateCutoff = now.minus(Duration.ofDays(validDays));
        final Instant graceCutoff = now.minus(Duration.ofMinutes(validOlderThanMinutes));
        final long boundaryRunId = findBoundaryRunId(dateCutoff);
        final List<Long> stageIds = findCandidateStageIds(boundaryRunId, dateCutoff, graceCutoff, validLimit);

        return FailedPublicationsResponse.builder()
                .days(validDays)
                .olderThanMinutes(validOlderThanMinutes)
                .limit(validLimit)
                .dateCutoff(dateCutoff)
                .graceCutoff(graceCutoff)
                .failedPublications(getFailedPublications(stageIds))
                .build();
    }

    /**
     * run_id grows with run_date, so the newest run older than dateCutoff minus one day bounds the primary-key
     * range without an index on run_date.
     */
    long findBoundaryRunId(Instant dateCutoff) {
        final Long runId = dsl.select(RUN.RUN_ID)
                .from(RUN)
                .where(RUN.RUN_DATE.lt(dateCutoff.minus(Duration.ofDays(1))))
                .orderBy(RUN.RUN_ID.desc())
                .limit(1)
                .fetchOne(RUN.RUN_ID);
        return runId == null ? 0L : runId;
    }

    ResultQuery<Record1<Long>> candidateStageIdsQuery(long boundaryRunId, Instant dateCutoff, Instant graceCutoff, int limit) {
        return dsl.select(STAGE.STAGE_ID)
                .from(RUN)
                .join(STAGE).on(STAGE.RUN_FK.eq(RUN.RUN_ID))
                .where(RUN.RUN_ID.gt(boundaryRunId))
                .and(RUN.RUN_DATE.ge(dateCutoff))
                .and(RUN.RUN_DATE.le(graceCutoff))
                .andExists(selectOne().from(STORAGE).where(STORAGE.STAGE_FK.eq(STAGE.STAGE_ID)))
                .andNotExists(selectOne().from(TEST_RESULT).where(TEST_RESULT.STAGE_FK.eq(STAGE.STAGE_ID)))
                .orderBy(STAGE.STAGE_ID.desc())
                .limit(limit);
    }

    List<Long> findCandidateStageIds(long boundaryRunId, Instant dateCutoff, Instant graceCutoff, int limit) {
        return candidateStageIdsQuery(boundaryRunId, dateCutoff, graceCutoff, limit).fetch(STAGE.STAGE_ID);
    }

    List<FailedPublication> getFailedPublications(List<Long> stageIds) {
        if (stageIds.isEmpty()) {
            return List.of();
        }
        final Map<Long, Map<String, FailedPublicationStorage>> storagesByStageId = getStoragesByStageId(stageIds);

        final List<FailedPublication> failedPublications = new ArrayList<>();
        for (Record record : dsl.select(COMPANY.COMPANY_NAME, ORG.ORG_NAME, REPO.REPO_NAME, BRANCH.BRANCH_NAME,
                                        JOB.JOB_ID, JOB.JOB_INFO, RUN.RUN_ID, RUN.RUN_REFERENCE, RUN.SHA, RUN.RUN_DATE,
                                        STAGE.STAGE_ID, STAGE.STAGE_NAME)
                .from(STAGE)
                .join(RUN).on(RUN.RUN_ID.eq(STAGE.RUN_FK))
                .join(JOB).on(JOB.JOB_ID.eq(RUN.JOB_FK))
                .join(BRANCH).on(BRANCH.BRANCH_ID.eq(JOB.BRANCH_FK))
                .join(REPO).on(REPO.REPO_ID.eq(BRANCH.REPO_FK))
                .join(ORG).on(ORG.ORG_ID.eq(REPO.ORG_FK))
                .join(COMPANY).on(COMPANY.COMPANY_ID.eq(ORG.COMPANY_FK))
                .where(STAGE.STAGE_ID.in(stageIds))
                .orderBy(STAGE.STAGE_ID.desc())
                .fetch()) {
            final Long stageId = record.get(STAGE.STAGE_ID);
            failedPublications.add(FailedPublication.builder()
                    .companyName(record.get(COMPANY.COMPANY_NAME))
                    .orgName(record.get(ORG.ORG_NAME))
                    .repoName(record.get(REPO.REPO_NAME))
                    .branchName(record.get(BRANCH.BRANCH_NAME))
                    .jobId(record.get(JOB.JOB_ID))
                    .jobInfo(record.get(JOB.JOB_INFO))
                    .runId(record.get(RUN.RUN_ID))
                    .runReference(record.get(RUN.RUN_REFERENCE))
                    .sha(record.get(RUN.SHA))
                    .runDate(record.get(RUN.RUN_DATE))
                    .stageId(stageId)
                    .stageName(record.get(STAGE.STAGE_NAME))
                    .storages(storagesByStageId.getOrDefault(stageId, Map.of()))
                    .build());
        }
        return failedPublications;
    }

    Map<Long, Map<String, FailedPublicationStorage>> getStoragesByStageId(List<Long> stageIds) {
        final Map<Long, Map<String, FailedPublicationStorage>> storagesByStageId = new HashMap<>();
        final List<StoragePojo> storages = dsl.selectFrom(STORAGE)
                .where(STORAGE.STAGE_FK.in(stageIds))
                .orderBy(STORAGE.LABEL)
                .fetchInto(StoragePojo.class);
        for (StoragePojo storage : storages) {
            final StorageType storageType = StorageType.fromStorageTypeId(storage.getStorageType());
            storagesByStageId.computeIfAbsent(storage.getStageFk(), k -> new LinkedHashMap<>())
                    .put(storage.getLabel(), FailedPublicationStorage.builder()
                            .storageId(storage.getStorageId())
                            .label(storage.getLabel())
                            .storageType(storageType == null ? null : storageType.name())
                            .isUploadComplete(storage.getIsUploadComplete())
                            .url(BrowseHtmlHelper.getStorageKey(storage))
                            .build());
        }
        return storagesByStageId;
    }
}
