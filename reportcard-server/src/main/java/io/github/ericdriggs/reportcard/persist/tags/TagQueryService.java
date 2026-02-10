package io.github.ericdriggs.reportcard.persist.tags;

import io.github.ericdriggs.reportcard.gen.db.tables.records.TestResultRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.jooq.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static io.github.ericdriggs.reportcard.gen.db.Tables.*;

/**
 * Service for tag-based test result queries.
 *
 * <p>Coordinates parsing of tag expressions and execution of database queries.
 * Uses {@link TagExpressionParser} to parse boolean expressions and
 * {@link TagQueryBuilder} to generate efficient SQL queries.
 *
 * <p>OR queries use UNION for index usage (see {@link TagQueryBuilder}).
 */
@Service
public class TagQueryService {

    private final DSLContext dsl;
    private final TagQueryBuilder queryBuilder;

    /**
     * Creates a TagQueryService with the given DSLContext.
     *
     * @param dsl the JOOQ DSLContext for database operations
     */
    @Autowired
    public TagQueryService(DSLContext dsl) {
        this.dsl = dsl;
        this.queryBuilder = new TagQueryBuilder(dsl);
    }

    /**
     * Find test results matching a tag expression.
     *
     * <p>Parses the expression and executes an efficient query using
     * UNION for OR expressions and single WHERE for AND expressions.
     *
     * @param expression Boolean tag expression (e.g., "smoke AND env=prod")
     * @return List of matching test result records
     * @throws ParseException if the expression is invalid
     */
    public List<TestResultRecord> findByTagExpression(String expression) {
        // Parse expression to AST
        TagExpr expr = TagExpressionParser.parse(expression);

        // Build and execute query
        Select<TestResultRecord> query = queryBuilder.buildQuery(expr);
        Result<TestResultRecord> results = query.fetch();

        return new ArrayList<>(results);
    }

    /**
     * Find test results matching a tag expression within a scope.
     *
     * <p>Filters results by optional hierarchy constraints (company, org, repo, branch).
     * Null values for scope filters indicate no filtering at that level.
     *
     * @param expression Boolean tag expression (e.g., "smoke AND env=prod")
     * @param companyId Scope filter (nullable for all companies)
     * @param orgId Scope filter (nullable)
     * @param repoId Scope filter (nullable)
     * @param branchId Scope filter (nullable)
     * @return List of matching test result records within scope
     * @throws ParseException if the expression is invalid
     */
    public List<TestResultRecord> findByTagExpression(
            String expression,
            Integer companyId,
            Integer orgId,
            Integer repoId,
            Integer branchId) {

        // Parse expression to AST
        TagExpr expr = TagExpressionParser.parse(expression);

        // Build base query from AST
        Select<TestResultRecord> baseQuery = queryBuilder.buildQuery(expr);

        // If no scope filters, execute directly
        if (companyId == null && orgId == null && repoId == null && branchId == null) {
            return new ArrayList<>(baseQuery.fetch());
        }

        // Build scoped query with joins
        // This wraps the tag-filtered query with hierarchy filtering
        var scopedQuery = dsl.selectFrom(TEST_RESULT)
            .where(TEST_RESULT.TEST_RESULT_ID.in(
                dsl.select(TEST_RESULT.TEST_RESULT_ID)
                    .from(baseQuery.asTable("tag_filtered"))
            ));

        // Add scope conditions via joins
        if (branchId != null) {
            scopedQuery = dsl.selectFrom(TEST_RESULT)
                .where(TEST_RESULT.TEST_RESULT_ID.in(
                    dsl.select(TEST_RESULT.TEST_RESULT_ID)
                        .from(TEST_RESULT)
                        .join(STAGE).on(STAGE.STAGE_ID.eq(TEST_RESULT.STAGE_FK))
                        .join(RUN).on(RUN.RUN_ID.eq(STAGE.RUN_FK))
                        .join(JOB).on(JOB.JOB_ID.eq(RUN.JOB_FK))
                        .join(BRANCH).on(BRANCH.BRANCH_ID.eq(JOB.BRANCH_FK))
                        .where(BRANCH.BRANCH_ID.eq(branchId))
                ))
                .and(TEST_RESULT.TEST_RESULT_ID.in(
                    dsl.select(TEST_RESULT.TEST_RESULT_ID)
                        .from(baseQuery.asTable("tag_filtered"))
                ));
        } else if (repoId != null) {
            scopedQuery = dsl.selectFrom(TEST_RESULT)
                .where(TEST_RESULT.TEST_RESULT_ID.in(
                    dsl.select(TEST_RESULT.TEST_RESULT_ID)
                        .from(TEST_RESULT)
                        .join(STAGE).on(STAGE.STAGE_ID.eq(TEST_RESULT.STAGE_FK))
                        .join(RUN).on(RUN.RUN_ID.eq(STAGE.RUN_FK))
                        .join(JOB).on(JOB.JOB_ID.eq(RUN.JOB_FK))
                        .join(BRANCH).on(BRANCH.BRANCH_ID.eq(JOB.BRANCH_FK))
                        .join(REPO).on(REPO.REPO_ID.eq(BRANCH.REPO_FK))
                        .where(REPO.REPO_ID.eq(repoId))
                ))
                .and(TEST_RESULT.TEST_RESULT_ID.in(
                    dsl.select(TEST_RESULT.TEST_RESULT_ID)
                        .from(baseQuery.asTable("tag_filtered"))
                ));
        } else if (orgId != null) {
            scopedQuery = dsl.selectFrom(TEST_RESULT)
                .where(TEST_RESULT.TEST_RESULT_ID.in(
                    dsl.select(TEST_RESULT.TEST_RESULT_ID)
                        .from(TEST_RESULT)
                        .join(STAGE).on(STAGE.STAGE_ID.eq(TEST_RESULT.STAGE_FK))
                        .join(RUN).on(RUN.RUN_ID.eq(STAGE.RUN_FK))
                        .join(JOB).on(JOB.JOB_ID.eq(RUN.JOB_FK))
                        .join(BRANCH).on(BRANCH.BRANCH_ID.eq(JOB.BRANCH_FK))
                        .join(REPO).on(REPO.REPO_ID.eq(BRANCH.REPO_FK))
                        .join(ORG).on(ORG.ORG_ID.eq(REPO.ORG_FK))
                        .where(ORG.ORG_ID.eq(orgId))
                ))
                .and(TEST_RESULT.TEST_RESULT_ID.in(
                    dsl.select(TEST_RESULT.TEST_RESULT_ID)
                        .from(baseQuery.asTable("tag_filtered"))
                ));
        } else if (companyId != null) {
            scopedQuery = dsl.selectFrom(TEST_RESULT)
                .where(TEST_RESULT.TEST_RESULT_ID.in(
                    dsl.select(TEST_RESULT.TEST_RESULT_ID)
                        .from(TEST_RESULT)
                        .join(STAGE).on(STAGE.STAGE_ID.eq(TEST_RESULT.STAGE_FK))
                        .join(RUN).on(RUN.RUN_ID.eq(STAGE.RUN_FK))
                        .join(JOB).on(JOB.JOB_ID.eq(RUN.JOB_FK))
                        .join(BRANCH).on(BRANCH.BRANCH_ID.eq(JOB.BRANCH_FK))
                        .join(REPO).on(REPO.REPO_ID.eq(BRANCH.REPO_FK))
                        .join(ORG).on(ORG.ORG_ID.eq(REPO.ORG_FK))
                        .join(COMPANY).on(COMPANY.COMPANY_ID.eq(ORG.COMPANY_FK))
                        .where(COMPANY.COMPANY_ID.eq(companyId))
                ))
                .and(TEST_RESULT.TEST_RESULT_ID.in(
                    dsl.select(TEST_RESULT.TEST_RESULT_ID)
                        .from(baseQuery.asTable("tag_filtered"))
                ));
        }

        return new ArrayList<>(scopedQuery.fetch());
    }

    /**
     * Get the query builder for advanced usage.
     *
     * @return the TagQueryBuilder instance
     */
    public TagQueryBuilder getQueryBuilder() {
        return queryBuilder;
    }
}
