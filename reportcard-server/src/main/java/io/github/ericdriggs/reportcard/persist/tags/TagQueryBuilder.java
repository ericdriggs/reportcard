package io.github.ericdriggs.reportcard.persist.tags;

import io.github.ericdriggs.reportcard.gen.db.tables.records.TestResultRecord;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Select;

import java.util.ArrayList;
import java.util.List;

import static org.jooq.impl.DSL.*;
import static io.github.ericdriggs.reportcard.gen.db.Tables.TEST_RESULT;

/**
 * Converts TagExpr AST to JOOQ queries.
 *
 * <p>IMPORTANT: OR queries must use UNION for index usage.
 * Single WHERE with OR does NOT use the multi-value index.
 *
 * <p>The multi-value index is defined as:
 * <pre>
 * CREATE INDEX idx_test_result_tags ON test_result (
 *     (CAST(tags->'$[*]' AS CHAR(25) ARRAY))
 * );
 * </pre>
 *
 * <p>Index usage patterns (verified with EXPLAIN):
 * <ul>
 *   <li>AND in single WHERE: uses index</li>
 *   <li>UNION of MEMBER OF: uses index for each leg</li>
 *   <li>OR in single WHERE: full table scan (no index)</li>
 * </ul>
 *
 * @see TagExpr
 * @see TagExpressionParser
 */
public class TagQueryBuilder {

    private final DSLContext dsl;

    /**
     * Creates a TagQueryBuilder with the given DSLContext.
     *
     * @param dsl the JOOQ DSLContext for building queries
     */
    public TagQueryBuilder(DSLContext dsl) {
        this.dsl = dsl;
    }

    /**
     * Build a MEMBER OF condition for a single tag.
     *
     * <p>Generates SQL: {@code {tag} MEMBER OF(test_result.tags)}
     *
     * @param tag the tag to match (e.g., "smoke" or "env=prod")
     * @return a JOOQ Condition for the MEMBER OF check
     */
    public Condition tagMatches(String tag) {
        // MEMBER OF query on JSON array
        // tags column stores JSON array like ["smoke", "regression", "env=prod"]
        return condition(
            "{0} MEMBER OF({1})",
            val(tag),
            field("tags")
        );
    }

    /**
     * Build a complete query from an AST expression.
     *
     * <p>OR nodes produce UNION queries for index usage.
     * AND nodes produce single WHERE with AND conditions.
     *
     * @param expr the parsed tag expression AST
     * @return a Select query ready for execution
     */
    public Select<TestResultRecord> buildQuery(TagExpr expr) {
        if (expr instanceof SimpleTag) {
            SimpleTag simpleTag = (SimpleTag) expr;
            return dsl.selectFrom(TEST_RESULT).where(tagMatches(simpleTag.tag()));
        } else if (expr instanceof AndExpr) {
            // AND: combine conditions in single WHERE
            Condition combined = buildCondition(expr);
            return dsl.selectFrom(TEST_RESULT).where(combined);
        } else if (expr instanceof OrExpr) {
            // OR: use UNION for index usage
            // Flatten all OR branches, build UNION
            List<TagExpr> orBranches = flattenOr(expr);
            Select<TestResultRecord> query = buildQueryForBranch(orBranches.get(0));
            for (int i = 1; i < orBranches.size(); i++) {
                // UNION removes duplicates
                query = query.union(buildQueryForBranch(orBranches.get(i)));
            }
            return query;
        } else {
            throw new IllegalArgumentException("Unknown TagExpr type: " + expr.getClass());
        }
    }

    /**
     * Build a Condition for use in WHERE clauses.
     *
     * <p>This method handles AND and SimpleTag expressions.
     * OR expressions should use buildQuery to generate UNION.
     *
     * @param expr the expression to convert (must not be OrExpr)
     * @return a JOOQ Condition
     * @throws IllegalArgumentException if expr is OrExpr
     */
    public Condition buildCondition(TagExpr expr) {
        if (expr instanceof SimpleTag) {
            SimpleTag simpleTag = (SimpleTag) expr;
            return tagMatches(simpleTag.tag());
        } else if (expr instanceof AndExpr) {
            AndExpr andExpr = (AndExpr) expr;
            return buildCondition(andExpr.left()).and(buildCondition(andExpr.right()));
        } else if (expr instanceof OrExpr) {
            throw new IllegalArgumentException(
                "OR in condition context requires UNION - use buildQuery instead");
        } else {
            throw new IllegalArgumentException("Unknown TagExpr type: " + expr.getClass());
        }
    }

    /**
     * Build query for a single OR branch (which may be AND or Simple).
     */
    private Select<TestResultRecord> buildQueryForBranch(TagExpr expr) {
        if (expr instanceof OrExpr) {
            throw new IllegalArgumentException("Nested OR should be flattened");
        }
        return dsl.selectFrom(TEST_RESULT).where(buildCondition(expr));
    }

    /**
     * Flatten nested OR expressions for UNION construction.
     *
     * <p>Transforms: {@code (a OR (b OR c))} to {@code [a, b, c]}
     *
     * @param expr the expression to flatten
     * @return list of non-OR branches
     */
    private List<TagExpr> flattenOr(TagExpr expr) {
        List<TagExpr> branches = new ArrayList<>();
        flattenOrHelper(expr, branches);
        return branches;
    }

    private void flattenOrHelper(TagExpr expr, List<TagExpr> branches) {
        if (expr instanceof OrExpr) {
            OrExpr orExpr = (OrExpr) expr;
            flattenOrHelper(orExpr.left(), branches);
            flattenOrHelper(orExpr.right(), branches);
        } else {
            branches.add(expr);
        }
    }
}
