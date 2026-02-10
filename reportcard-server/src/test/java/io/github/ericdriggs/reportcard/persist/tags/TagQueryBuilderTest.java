package io.github.ericdriggs.reportcard.persist.tags;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TagQueryBuilder.
 *
 * <p>Verifies correct SQL generation patterns:
 * <ul>
 *   <li>AND expressions use single WHERE with AND conditions</li>
 *   <li>OR expressions use UNION for multi-value index usage</li>
 *   <li>MEMBER OF conditions correctly formatted</li>
 * </ul>
 *
 * <p>These are unit tests - no database connection required.
 * Uses JOOQ's DSL.using(SQLDialect.MYSQL) to build queries for inspection.
 */
class TagQueryBuilderTest {

    private TagQueryBuilder queryBuilder;

    @BeforeEach
    void setUp() {
        // Use MYSQL dialect for query building verification
        DSLContext dsl = DSL.using(SQLDialect.MYSQL);
        queryBuilder = new TagQueryBuilder(dsl);
    }

    @Nested
    @DisplayName("MEMBER OF condition generation")
    class MemberOfConditionTests {

        @Test
        @DisplayName("generates MEMBER OF condition for simple tag")
        void simpleTagMemberOf() {
            Condition condition = queryBuilder.tagMatches("smoke");

            String sql = condition.toString();
            // Verify MEMBER OF pattern
            assertTrue(sql.contains("MEMBER OF"), "Should use MEMBER OF: " + sql);
            assertTrue(sql.contains("'smoke'"), "Should contain tag value: " + sql);
        }

        @Test
        @DisplayName("generates MEMBER OF condition for key=value tag")
        void keyValueTagMemberOf() {
            Condition condition = queryBuilder.tagMatches("env=prod");

            String sql = condition.toString();
            assertTrue(sql.contains("MEMBER OF"), "Should use MEMBER OF: " + sql);
            assertTrue(sql.contains("'env=prod'"), "Should contain key=value: " + sql);
        }

        @Test
        @DisplayName("tags field is referenced correctly")
        void tagsFieldReference() {
            Condition condition = queryBuilder.tagMatches("smoke");

            String sql = condition.toString();
            assertTrue(sql.contains("tags"), "Should reference tags field: " + sql);
        }
    }

    @Nested
    @DisplayName("AND expression query generation")
    class AndExpressionTests {

        @Test
        @DisplayName("AND generates single WHERE clause")
        void andUsesSingleWhere() {
            TagExpr expr = new AndExpr(
                new SimpleTag("smoke"),
                new SimpleTag("env=prod")
            );

            Select<?> query = queryBuilder.buildQuery(expr);
            String sql = query.getSQL();

            // AND should NOT use UNION
            assertFalse(sql.toLowerCase().contains("union"),
                "AND should not use UNION: " + sql);
        }

        @Test
        @DisplayName("AND combines multiple conditions")
        void andCombinesConditions() {
            TagExpr expr = new AndExpr(
                new SimpleTag("smoke"),
                new SimpleTag("env=prod")
            );

            Select<?> query = queryBuilder.buildQuery(expr);
            String sql = query.getSQL();

            // Should have both MEMBER OF checks
            // Count occurrences of MEMBER OF
            int memberOfCount = countOccurrences(sql, "MEMBER OF");
            assertEquals(2, memberOfCount, "Should have 2 MEMBER OF conditions: " + sql);
        }

        @Test
        @DisplayName("chained AND expressions stay in single WHERE")
        void chainedAndStaysInSingleWhere() {
            // (a AND b) AND c
            TagExpr expr = new AndExpr(
                new AndExpr(
                    new SimpleTag("smoke"),
                    new SimpleTag("regression")
                ),
                new SimpleTag("env=prod")
            );

            Select<?> query = queryBuilder.buildQuery(expr);
            String sql = query.getSQL();

            assertFalse(sql.toLowerCase().contains("union"),
                "Chained AND should not use UNION: " + sql);
            assertEquals(3, countOccurrences(sql, "MEMBER OF"),
                "Should have 3 MEMBER OF conditions: " + sql);
        }

        @Test
        @DisplayName("buildCondition works for AND")
        void buildConditionForAnd() {
            TagExpr expr = new AndExpr(
                new SimpleTag("smoke"),
                new SimpleTag("env=prod")
            );

            Condition condition = queryBuilder.buildCondition(expr);
            String sql = condition.toString();

            // Should be combined with AND
            assertTrue(sql.toLowerCase().contains("and"),
                "Should use AND: " + sql);
        }
    }

    @Nested
    @DisplayName("OR expression query generation")
    class OrExpressionTests {

        @Test
        @DisplayName("OR generates UNION query")
        void orUsesUnion() {
            TagExpr expr = new OrExpr(
                new SimpleTag("smoke"),
                new SimpleTag("regression")
            );

            Select<?> query = queryBuilder.buildQuery(expr);
            String sql = query.getSQL();

            // OR should use UNION
            assertTrue(sql.toLowerCase().contains("union"),
                "OR should use UNION for index usage: " + sql);
        }

        @Test
        @DisplayName("chained OR expressions flatten to single UNION")
        void chainedOrFlattensToUnion() {
            // (a OR b) OR c
            TagExpr expr = new OrExpr(
                new OrExpr(
                    new SimpleTag("smoke"),
                    new SimpleTag("regression")
                ),
                new SimpleTag("critical")
            );

            Select<?> query = queryBuilder.buildQuery(expr);
            String sql = query.getSQL().toLowerCase();

            // Should have at least 2 UNION keywords for 3 branches
            int unionCount = countOccurrences(sql, "union");
            assertEquals(2, unionCount,
                "3 OR branches should produce 2 UNIONs: " + sql);
        }

        @Test
        @DisplayName("buildCondition throws for OR")
        void buildConditionThrowsForOr() {
            TagExpr expr = new OrExpr(
                new SimpleTag("smoke"),
                new SimpleTag("regression")
            );

            IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> queryBuilder.buildCondition(expr)
            );

            assertTrue(ex.getMessage().contains("UNION"),
                "Error should mention UNION: " + ex.getMessage());
        }
    }

    @Nested
    @DisplayName("Complex expression handling")
    class ComplexExpressionTests {

        @Test
        @DisplayName("OR with AND branches generates UNION with AND in each leg")
        void orWithAndBranches() {
            // (smoke AND env=staging) OR (regression AND env=prod)
            TagExpr expr = new OrExpr(
                new AndExpr(new SimpleTag("smoke"), new SimpleTag("env=staging")),
                new AndExpr(new SimpleTag("regression"), new SimpleTag("env=prod"))
            );

            Select<?> query = queryBuilder.buildQuery(expr);
            String sql = query.getSQL().toLowerCase();

            // Should use UNION
            assertTrue(sql.contains("union"),
                "OR at top level should use UNION: " + sql);

            // Should have 4 MEMBER OF conditions total (2 per leg)
            int memberOfCount = countOccurrences(sql, "member of");
            assertEquals(4, memberOfCount,
                "Should have 4 MEMBER OF conditions: " + sql);
        }

        @Test
        @DisplayName("AND at top level with OR child requires special handling")
        void andWithOrChild() {
            // (smoke OR regression) AND env=prod
            // This is a complex case - AND with OR child
            TagExpr expr = new AndExpr(
                new OrExpr(
                    new SimpleTag("smoke"),
                    new SimpleTag("regression")
                ),
                new SimpleTag("env=prod")
            );

            // buildCondition should throw since it has OR child
            assertThrows(IllegalArgumentException.class,
                () -> queryBuilder.buildCondition(expr),
                "AND with OR child should throw in buildCondition");

            // buildQuery on AndExpr with OrExpr child will also throw
            // because buildCondition is called internally
            assertThrows(IllegalArgumentException.class,
                () -> queryBuilder.buildQuery(expr),
                "AND with OR child requires query rewriting");
        }

        @Test
        @DisplayName("deeply nested AND expressions work")
        void deeplyNestedAnd() {
            // ((a AND b) AND c) AND d
            TagExpr expr = new AndExpr(
                new AndExpr(
                    new AndExpr(
                        new SimpleTag("a"),
                        new SimpleTag("b")
                    ),
                    new SimpleTag("c")
                ),
                new SimpleTag("d")
            );

            Select<?> query = queryBuilder.buildQuery(expr);
            String sql = query.getSQL();

            assertFalse(sql.toLowerCase().contains("union"),
                "Deeply nested AND should not use UNION: " + sql);
            assertEquals(4, countOccurrences(sql, "MEMBER OF"),
                "Should have 4 MEMBER OF conditions: " + sql);
        }

        @Test
        @DisplayName("deeply nested OR expressions flatten")
        void deeplyNestedOrFlattens() {
            // ((a OR b) OR c) OR d
            TagExpr expr = new OrExpr(
                new OrExpr(
                    new OrExpr(
                        new SimpleTag("a"),
                        new SimpleTag("b")
                    ),
                    new SimpleTag("c")
                ),
                new SimpleTag("d")
            );

            Select<?> query = queryBuilder.buildQuery(expr);
            String sql = query.getSQL().toLowerCase();

            // Should have 3 UNIONs for 4 branches
            int unionCount = countOccurrences(sql, "union");
            assertEquals(3, unionCount,
                "4 OR branches should produce 3 UNIONs: " + sql);
        }
    }

    @Nested
    @DisplayName("SimpleTag query generation")
    class SimpleTagTests {

        @Test
        @DisplayName("simple tag generates basic query")
        void simpleTagGeneratesQuery() {
            TagExpr expr = new SimpleTag("smoke");

            Select<?> query = queryBuilder.buildQuery(expr);
            String sql = query.getSQL();

            assertNotNull(sql);
            assertTrue(sql.contains("test_result") || sql.contains("TEST_RESULT"),
                "Should select from test_result: " + sql);
            assertTrue(sql.contains("MEMBER OF"),
                "Should use MEMBER OF: " + sql);
        }

        @Test
        @DisplayName("simple tag buildCondition works")
        void simpleTagBuildCondition() {
            TagExpr expr = new SimpleTag("smoke");

            Condition condition = queryBuilder.buildCondition(expr);
            String sql = condition.toString();

            assertTrue(sql.contains("MEMBER OF"),
                "Should use MEMBER OF: " + sql);
            assertTrue(sql.contains("'smoke'"),
                "Should contain tag value: " + sql);
        }
    }

    @Nested
    @DisplayName("Parser integration")
    class ParserIntegrationTests {

        @Test
        @DisplayName("parsed AND expression generates correct query")
        void parsedAndExpression() {
            String expression = "smoke AND env=prod";
            TagExpr expr = TagExpressionParser.parse(expression);

            assertInstanceOf(AndExpr.class, expr);

            Select<?> query = queryBuilder.buildQuery(expr);
            String sql = query.getSQL();

            assertFalse(sql.toLowerCase().contains("union"),
                "AND should not use UNION: " + sql);
        }

        @Test
        @DisplayName("parsed OR expression generates correct query")
        void parsedOrExpression() {
            String expression = "smoke OR regression";
            TagExpr expr = TagExpressionParser.parse(expression);

            assertInstanceOf(OrExpr.class, expr);

            Select<?> query = queryBuilder.buildQuery(expr);
            String sql = query.getSQL().toLowerCase();

            assertTrue(sql.contains("union"),
                "OR should use UNION: " + sql);
        }

        @Test
        @DisplayName("complex parsed expression works")
        void complexParsedExpression() {
            // (smoke AND env=staging) OR (regression AND env=prod)
            String expression = "(smoke AND env=staging) OR (regression AND env=prod)";
            TagExpr expr = TagExpressionParser.parse(expression);

            assertInstanceOf(OrExpr.class, expr);

            Select<?> query = queryBuilder.buildQuery(expr);
            String sql = query.getSQL().toLowerCase();

            assertTrue(sql.contains("union"),
                "Top-level OR should use UNION: " + sql);
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
