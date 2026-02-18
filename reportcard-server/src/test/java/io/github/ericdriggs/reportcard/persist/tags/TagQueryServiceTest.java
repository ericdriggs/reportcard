package io.github.ericdriggs.reportcard.persist.tags;

import io.github.ericdriggs.reportcard.gen.db.tables.records.TestResultRecord;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TagQueryService.
 *
 * <p>Tests the service layer coordination between:
 * <ul>
 *   <li>{@link TagExpressionParser} - parses expression strings to AST</li>
 *   <li>{@link TagQueryBuilder} - converts AST to JOOQ queries</li>
 * </ul>
 *
 * <p>Uses real parser and query builder with mocked/real DSLContext for query inspection.
 * These are unit tests - no database connection required.
 */
@ExtendWith(MockitoExtension.class)
class TagQueryServiceTest {

    private DSLContext dsl;
    private TagQueryService service;

    @BeforeEach
    void setUp() {
        // Use real DSLContext with MYSQL dialect for query building
        dsl = DSL.using(SQLDialect.MYSQL);
        service = new TagQueryService(dsl);
    }

    @Nested
    @DisplayName("Service instantiation")
    class ServiceInstantiationTests {

        @Test
        @DisplayName("service creates query builder")
        void serviceCreatesQueryBuilder() {
            assertNotNull(service.getQueryBuilder(),
                "Service should create query builder on instantiation");
        }

        @Test
        @DisplayName("query builder uses same DSLContext")
        void queryBuilderUsesSameDsl() {
            TagQueryBuilder builder = service.getQueryBuilder();
            // Verify builder can generate queries (uses DSLContext internally)
            Select<TestResultRecord> query = builder.buildQuery(new SimpleTag("test"));
            assertNotNull(query, "Builder should generate queries");
        }
    }

    @Nested
    @DisplayName("Expression parsing integration")
    class ExpressionParsingIntegrationTests {

        @Test
        @DisplayName("simple tag expression uses MEMBER OF")
        void simpleTagExpression() {
            String expression = "smoke";
            TagExpr parsed = TagExpressionParser.parse(expression);

            assertInstanceOf(SimpleTag.class, parsed);
            Select<?> query = service.getQueryBuilder().buildQuery(parsed);
            String sql = query.getSQL();

            assertTrue(sql.contains("MEMBER OF"),
                "Simple tag should use MEMBER OF: " + sql);
        }

        @Test
        @DisplayName("AND expression generates single WHERE")
        void andExpressionGeneratesSingleWhere() {
            String expression = "smoke AND env=prod";
            TagExpr parsed = TagExpressionParser.parse(expression);

            assertInstanceOf(AndExpr.class, parsed);
            Select<?> query = service.getQueryBuilder().buildQuery(parsed);
            String sql = query.getSQL();

            // AND should NOT use UNION (uses index efficiently with single WHERE)
            assertFalse(sql.toLowerCase().contains("union"),
                "AND expression should use single WHERE, not UNION: " + sql);
            assertTrue(countOccurrences(sql, "MEMBER OF") >= 2,
                "Should have multiple MEMBER OF conditions: " + sql);
        }

        @Test
        @DisplayName("OR expression generates UNION for index usage")
        void orExpressionGeneratesUnion() {
            String expression = "smoke OR regression";
            TagExpr parsed = TagExpressionParser.parse(expression);

            assertInstanceOf(OrExpr.class, parsed);
            Select<?> query = service.getQueryBuilder().buildQuery(parsed);
            String sql = query.getSQL().toLowerCase();

            // OR MUST use UNION for multi-value index usage
            assertTrue(sql.contains("union"),
                "OR expression MUST use UNION for index usage: " + sql);
        }

        @Test
        @DisplayName("key=value tags work like simple tags")
        void keyValueTagsWorkLikeSimpleTags() {
            String expression = "env=prod";
            TagExpr parsed = TagExpressionParser.parse(expression);

            assertInstanceOf(SimpleTag.class, parsed);
            SimpleTag tag = (SimpleTag) parsed;
            assertEquals("env=prod", tag.tag(),
                "Key=value should be stored as single string");

            Select<?> query = service.getQueryBuilder().buildQuery(parsed);
            String sql = query.getSQL();

            // JOOQ uses parameterized queries, so value appears as ? not literal
            // The important thing is MEMBER OF pattern is used
            assertTrue(sql.contains("MEMBER OF"),
                "Query should use MEMBER OF for key=value: " + sql);
        }

        @Test
        @DisplayName("complex expression with parentheses")
        void complexExpressionWithParentheses() {
            // (smoke AND env=staging) OR (regression AND env=prod)
            String expression = "(smoke AND env=staging) OR (regression AND env=prod)";
            TagExpr parsed = TagExpressionParser.parse(expression);

            assertInstanceOf(OrExpr.class, parsed);
            Select<?> query = service.getQueryBuilder().buildQuery(parsed);
            String sql = query.getSQL().toLowerCase();

            // Top-level OR should use UNION
            assertTrue(sql.contains("union"),
                "Complex OR at top should use UNION: " + sql);
        }
    }

    @Nested
    @DisplayName("Query pattern verification")
    class QueryPatternVerificationTests {

        @Test
        @DisplayName("OR with 3 branches produces 2 UNIONs")
        void threeOrBranchesProduceTwoUnions() {
            String expression = "smoke OR regression OR critical";
            TagExpr parsed = TagExpressionParser.parse(expression);

            Select<?> query = service.getQueryBuilder().buildQuery(parsed);
            String sql = query.getSQL().toLowerCase();

            int unionCount = countOccurrences(sql, "union");
            assertEquals(2, unionCount,
                "3 OR branches should produce 2 UNIONs: " + sql);
        }

        @Test
        @DisplayName("AND with 3 branches stays in single WHERE")
        void threeAndBranchesStayInSingleWhere() {
            String expression = "smoke AND regression AND env=prod";
            TagExpr parsed = TagExpressionParser.parse(expression);

            Select<?> query = service.getQueryBuilder().buildQuery(parsed);
            String sql = query.getSQL();

            assertFalse(sql.toLowerCase().contains("union"),
                "AND branches should not use UNION: " + sql);
            assertTrue(countOccurrences(sql, "MEMBER OF") >= 3,
                "Should have 3+ MEMBER OF conditions: " + sql);
        }

        @Test
        @DisplayName("precedence: AND binds tighter than OR")
        void andBindsTighterThanOr() {
            // a OR b AND c parses as a OR (b AND c)
            String expression = "smoke OR regression AND env=prod";
            TagExpr parsed = TagExpressionParser.parse(expression);

            // Should be OrExpr with SimpleTag left and AndExpr right
            assertInstanceOf(OrExpr.class, parsed);
            OrExpr or = (OrExpr) parsed;
            assertInstanceOf(SimpleTag.class, or.left());
            assertInstanceOf(AndExpr.class, or.right());
        }
    }

    @Nested
    @DisplayName("Error handling")
    class ErrorHandlingTests {

        @Test
        @DisplayName("empty expression throws ParseException")
        void emptyExpressionThrows() {
            assertThrows(ParseException.class,
                () -> TagExpressionParser.parse(""));
        }

        @Test
        @DisplayName("null expression throws ParseException")
        void nullExpressionThrows() {
            assertThrows(ParseException.class,
                () -> TagExpressionParser.parse(null));
        }

        @Test
        @DisplayName("unbalanced parentheses throws ParseException")
        void unbalancedParenthesesThrows() {
            assertThrows(ParseException.class,
                () -> TagExpressionParser.parse("(smoke AND env=prod"));
        }

        @Test
        @DisplayName("consecutive operators throw ParseException")
        void consecutiveOperatorsThrow() {
            assertThrows(ParseException.class,
                () -> TagExpressionParser.parse("smoke AND AND env=prod"));
        }
    }

    @Nested
    @DisplayName("Index usage patterns (critical for performance)")
    class IndexUsagePatternsTests {

        @Test
        @DisplayName("UNION preserves index usage for OR - each leg queries separately")
        void unionPreservesIndexUsage() {
            // This is the critical insight from Phase 7 research:
            // OR in single WHERE = full table scan (no index)
            // UNION of MEMBER OF = index lookup per leg

            String expression = "smoke OR regression OR critical";
            TagExpr parsed = TagExpressionParser.parse(expression);
            Select<?> query = service.getQueryBuilder().buildQuery(parsed);
            String sql = query.getSQL().toLowerCase();

            // Each leg should be a separate SELECT
            // Count select from test_result
            int selectCount = countOccurrences(sql, "select");
            assertTrue(selectCount >= 3,
                "UNION should produce multiple SELECTs: " + sql);
        }

        @Test
        @DisplayName("AND in single WHERE uses index efficiently")
        void andInSingleWhereUsesIndex() {
            // Multiple AND conditions in single WHERE can still use index
            // because MySQL can intersect index lookups

            String expression = "smoke AND regression AND env=prod";
            TagExpr parsed = TagExpressionParser.parse(expression);
            Select<?> query = service.getQueryBuilder().buildQuery(parsed);
            String sql = query.getSQL().toLowerCase();

            // Should be single SELECT (no UNION overhead)
            int selectCount = countOccurrences(sql, "select");
            assertEquals(1, selectCount,
                "AND should use single SELECT: " + sql);
        }

        @Test
        @DisplayName("OR branches with AND conditions produce efficient queries")
        void orWithAndConditionsProducesEfficientQueries() {
            // (a AND b) OR (c AND d) should be:
            // SELECT ... WHERE a AND b UNION SELECT ... WHERE c AND d
            // NOT: SELECT ... WHERE (a AND b) OR (c AND d)

            String expression = "(smoke AND env=staging) OR (regression AND env=prod)";
            TagExpr parsed = TagExpressionParser.parse(expression);
            Select<?> query = service.getQueryBuilder().buildQuery(parsed);
            String sql = query.getSQL().toLowerCase();

            // Should use UNION (one for the OR)
            assertEquals(1, countOccurrences(sql, "union"),
                "Should use exactly 1 UNION: " + sql);

            // Should have 4 MEMBER OF conditions (2 per leg)
            assertEquals(4, countOccurrences(sql, "member of"),
                "Should have 4 MEMBER OF conditions: " + sql);
        }
    }

    /**
     * Count occurrences of a substring in a string (case-insensitive).
     */
    private int countOccurrences(String text, String pattern) {
        String lowerText = text.toLowerCase();
        String lowerPattern = pattern.toLowerCase();
        int count = 0;
        int index = 0;
        while ((index = lowerText.indexOf(lowerPattern, index)) != -1) {
            count++;
            index += lowerPattern.length();
        }
        return count;
    }
}
