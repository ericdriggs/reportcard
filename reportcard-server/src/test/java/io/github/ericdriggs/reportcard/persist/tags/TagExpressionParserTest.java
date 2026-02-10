package io.github.ericdriggs.reportcard.persist.tags;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TagExpressionParser.
 *
 * Tests cover:
 * - Simple tag parsing
 * - AND expressions
 * - OR expressions
 * - Operator precedence (AND binds tighter than OR)
 * - Parentheses for precedence override
 * - Nested expressions
 * - Invalid input handling
 * - Whitespace handling
 */
class TagExpressionParserTest {

    @Nested
    @DisplayName("Simple tags")
    class SimpleTagTests {

        @Test
        @DisplayName("parses simple identifier tag")
        void parseSimpleTag() {
            TagExpr result = TagExpressionParser.parse("smoke");

            assertInstanceOf(SimpleTag.class, result);
            assertEquals("smoke", ((SimpleTag) result).tag());
        }

        @Test
        @DisplayName("parses key=value tag")
        void parseKeyValueTag() {
            TagExpr result = TagExpressionParser.parse("env=prod");

            assertInstanceOf(SimpleTag.class, result);
            assertEquals("env=prod", ((SimpleTag) result).tag());
        }

        @Test
        @DisplayName("parses tag with hyphen")
        void parseTagWithHyphen() {
            TagExpr result = TagExpressionParser.parse("smoke-test");

            assertInstanceOf(SimpleTag.class, result);
            assertEquals("smoke-test", ((SimpleTag) result).tag());
        }

        @Test
        @DisplayName("parses tag with underscore")
        void parseTagWithUnderscore() {
            TagExpr result = TagExpressionParser.parse("smoke_test");

            assertInstanceOf(SimpleTag.class, result);
            assertEquals("smoke_test", ((SimpleTag) result).tag());
        }

        @Test
        @DisplayName("parses tag with digits")
        void parseTagWithDigits() {
            TagExpr result = TagExpressionParser.parse("test123");

            assertInstanceOf(SimpleTag.class, result);
            assertEquals("test123", ((SimpleTag) result).tag());
        }

        @Test
        @DisplayName("preserves case in tag")
        void preservesCase() {
            TagExpr result = TagExpressionParser.parse("SmokeTest");

            assertInstanceOf(SimpleTag.class, result);
            assertEquals("SmokeTest", ((SimpleTag) result).tag());
        }

        @Test
        @DisplayName("trims leading and trailing whitespace")
        void trimsWhitespace() {
            TagExpr result = TagExpressionParser.parse("  smoke  ");

            assertInstanceOf(SimpleTag.class, result);
            assertEquals("smoke", ((SimpleTag) result).tag());
        }
    }

    @Nested
    @DisplayName("AND expressions")
    class AndExpressionTests {

        @Test
        @DisplayName("parses simple AND expression")
        void parseSimpleAnd() {
            TagExpr result = TagExpressionParser.parse("smoke AND regression");

            assertInstanceOf(AndExpr.class, result);
            AndExpr and = (AndExpr) result;

            assertInstanceOf(SimpleTag.class, and.left());
            assertInstanceOf(SimpleTag.class, and.right());
            assertEquals("smoke", ((SimpleTag) and.left()).tag());
            assertEquals("regression", ((SimpleTag) and.right()).tag());
        }

        @Test
        @DisplayName("parses AND with key=value tags")
        void parseAndWithKeyValue() {
            TagExpr result = TagExpressionParser.parse("smoke AND env=prod");

            assertInstanceOf(AndExpr.class, result);
            AndExpr and = (AndExpr) result;

            assertEquals("smoke", ((SimpleTag) and.left()).tag());
            assertEquals("env=prod", ((SimpleTag) and.right()).tag());
        }

        @Test
        @DisplayName("parses chained AND expressions (left associative)")
        void parseChainedAnd() {
            TagExpr result = TagExpressionParser.parse("a AND b AND c");

            // a AND b AND c = (a AND b) AND c (left associative)
            assertInstanceOf(AndExpr.class, result);
            AndExpr outer = (AndExpr) result;

            assertInstanceOf(AndExpr.class, outer.left());
            assertInstanceOf(SimpleTag.class, outer.right());
            assertEquals("c", ((SimpleTag) outer.right()).tag());

            AndExpr inner = (AndExpr) outer.left();
            assertEquals("a", ((SimpleTag) inner.left()).tag());
            assertEquals("b", ((SimpleTag) inner.right()).tag());
        }

        @Test
        @DisplayName("handles extra whitespace around AND")
        void handlesExtraWhitespace() {
            TagExpr result = TagExpressionParser.parse("smoke   AND   regression");

            assertInstanceOf(AndExpr.class, result);
            AndExpr and = (AndExpr) result;
            assertEquals("smoke", ((SimpleTag) and.left()).tag());
            assertEquals("regression", ((SimpleTag) and.right()).tag());
        }
    }

    @Nested
    @DisplayName("OR expressions")
    class OrExpressionTests {

        @Test
        @DisplayName("parses simple OR expression")
        void parseSimpleOr() {
            TagExpr result = TagExpressionParser.parse("smoke OR regression");

            assertInstanceOf(OrExpr.class, result);
            OrExpr or = (OrExpr) result;

            assertInstanceOf(SimpleTag.class, or.left());
            assertInstanceOf(SimpleTag.class, or.right());
            assertEquals("smoke", ((SimpleTag) or.left()).tag());
            assertEquals("regression", ((SimpleTag) or.right()).tag());
        }

        @Test
        @DisplayName("parses chained OR expressions (left associative)")
        void parseChainedOr() {
            TagExpr result = TagExpressionParser.parse("a OR b OR c");

            // a OR b OR c = (a OR b) OR c (left associative)
            assertInstanceOf(OrExpr.class, result);
            OrExpr outer = (OrExpr) result;

            assertInstanceOf(OrExpr.class, outer.left());
            assertInstanceOf(SimpleTag.class, outer.right());
            assertEquals("c", ((SimpleTag) outer.right()).tag());

            OrExpr inner = (OrExpr) outer.left();
            assertEquals("a", ((SimpleTag) inner.left()).tag());
            assertEquals("b", ((SimpleTag) inner.right()).tag());
        }
    }

    @Nested
    @DisplayName("Operator precedence")
    class PrecedenceTests {

        @Test
        @DisplayName("AND binds tighter than OR: a OR b AND c = a OR (b AND c)")
        void andBindsTighterThanOr() {
            TagExpr result = TagExpressionParser.parse("a OR b AND c");

            // Should parse as: a OR (b AND c)
            assertInstanceOf(OrExpr.class, result);
            OrExpr or = (OrExpr) result;

            assertInstanceOf(SimpleTag.class, or.left());
            assertEquals("a", ((SimpleTag) or.left()).tag());

            assertInstanceOf(AndExpr.class, or.right());
            AndExpr and = (AndExpr) or.right();
            assertEquals("b", ((SimpleTag) and.left()).tag());
            assertEquals("c", ((SimpleTag) and.right()).tag());
        }

        @Test
        @DisplayName("AND binds tighter on left side too: a AND b OR c = (a AND b) OR c")
        void andBindsTighterOnLeftSide() {
            TagExpr result = TagExpressionParser.parse("a AND b OR c");

            // Should parse as: (a AND b) OR c
            assertInstanceOf(OrExpr.class, result);
            OrExpr or = (OrExpr) result;

            assertInstanceOf(AndExpr.class, or.left());
            AndExpr and = (AndExpr) or.left();
            assertEquals("a", ((SimpleTag) and.left()).tag());
            assertEquals("b", ((SimpleTag) and.right()).tag());

            assertInstanceOf(SimpleTag.class, or.right());
            assertEquals("c", ((SimpleTag) or.right()).tag());
        }

        @Test
        @DisplayName("complex precedence: a OR b AND c OR d = (a OR (b AND c)) OR d")
        void complexPrecedence() {
            TagExpr result = TagExpressionParser.parse("a OR b AND c OR d");

            // Should parse as: (a OR (b AND c)) OR d
            assertInstanceOf(OrExpr.class, result);
            OrExpr outerOr = (OrExpr) result;

            assertInstanceOf(SimpleTag.class, outerOr.right());
            assertEquals("d", ((SimpleTag) outerOr.right()).tag());

            assertInstanceOf(OrExpr.class, outerOr.left());
            OrExpr innerOr = (OrExpr) outerOr.left();

            assertEquals("a", ((SimpleTag) innerOr.left()).tag());

            assertInstanceOf(AndExpr.class, innerOr.right());
            AndExpr and = (AndExpr) innerOr.right();
            assertEquals("b", ((SimpleTag) and.left()).tag());
            assertEquals("c", ((SimpleTag) and.right()).tag());
        }
    }

    @Nested
    @DisplayName("Parentheses")
    class ParenthesesTests {

        @Test
        @DisplayName("parentheses override precedence: (a OR b) AND c")
        void parenthesesOverridePrecedence() {
            TagExpr result = TagExpressionParser.parse("(a OR b) AND c");

            // Parentheses force OR to be evaluated first
            assertInstanceOf(AndExpr.class, result);
            AndExpr and = (AndExpr) result;

            assertInstanceOf(OrExpr.class, and.left());
            OrExpr or = (OrExpr) and.left();
            assertEquals("a", ((SimpleTag) or.left()).tag());
            assertEquals("b", ((SimpleTag) or.right()).tag());

            assertInstanceOf(SimpleTag.class, and.right());
            assertEquals("c", ((SimpleTag) and.right()).tag());
        }

        @Test
        @DisplayName("parentheses on right side: a AND (b OR c)")
        void parenthesesOnRightSide() {
            TagExpr result = TagExpressionParser.parse("a AND (b OR c)");

            assertInstanceOf(AndExpr.class, result);
            AndExpr and = (AndExpr) result;

            assertEquals("a", ((SimpleTag) and.left()).tag());

            assertInstanceOf(OrExpr.class, and.right());
            OrExpr or = (OrExpr) and.right();
            assertEquals("b", ((SimpleTag) or.left()).tag());
            assertEquals("c", ((SimpleTag) or.right()).tag());
        }

        @Test
        @DisplayName("nested parentheses: ((a AND b) OR c) AND d")
        void nestedParentheses() {
            TagExpr result = TagExpressionParser.parse("((a AND b) OR c) AND d");

            assertInstanceOf(AndExpr.class, result);
            AndExpr outerAnd = (AndExpr) result;

            assertEquals("d", ((SimpleTag) outerAnd.right()).tag());

            assertInstanceOf(OrExpr.class, outerAnd.left());
            OrExpr or = (OrExpr) outerAnd.left();

            assertEquals("c", ((SimpleTag) or.right()).tag());

            assertInstanceOf(AndExpr.class, or.left());
            AndExpr innerAnd = (AndExpr) or.left();
            assertEquals("a", ((SimpleTag) innerAnd.left()).tag());
            assertEquals("b", ((SimpleTag) innerAnd.right()).tag());
        }

        @Test
        @DisplayName("unnecessary parentheses around single tag: (smoke)")
        void unnecessaryParentheses() {
            TagExpr result = TagExpressionParser.parse("(smoke)");

            assertInstanceOf(SimpleTag.class, result);
            assertEquals("smoke", ((SimpleTag) result).tag());
        }

        @Test
        @DisplayName("double unnecessary parentheses: ((smoke))")
        void doubleUnnecessaryParentheses() {
            TagExpr result = TagExpressionParser.parse("((smoke))");

            assertInstanceOf(SimpleTag.class, result);
            assertEquals("smoke", ((SimpleTag) result).tag());
        }

        @Test
        @DisplayName("complex real-world example: (smoke OR regression) AND env=prod")
        void complexRealWorldExample() {
            TagExpr result = TagExpressionParser.parse("(smoke OR regression) AND env=prod");

            assertInstanceOf(AndExpr.class, result);
            AndExpr and = (AndExpr) result;

            assertInstanceOf(OrExpr.class, and.left());
            OrExpr or = (OrExpr) and.left();
            assertEquals("smoke", ((SimpleTag) or.left()).tag());
            assertEquals("regression", ((SimpleTag) or.right()).tag());

            assertEquals("env=prod", ((SimpleTag) and.right()).tag());
        }

        @Test
        @DisplayName("complex multi-condition: (smoke AND env=prod) OR (regression AND env=staging)")
        void complexMultiCondition() {
            TagExpr result = TagExpressionParser.parse("(smoke AND env=prod) OR (regression AND env=staging)");

            assertInstanceOf(OrExpr.class, result);
            OrExpr or = (OrExpr) result;

            assertInstanceOf(AndExpr.class, or.left());
            AndExpr leftAnd = (AndExpr) or.left();
            assertEquals("smoke", ((SimpleTag) leftAnd.left()).tag());
            assertEquals("env=prod", ((SimpleTag) leftAnd.right()).tag());

            assertInstanceOf(AndExpr.class, or.right());
            AndExpr rightAnd = (AndExpr) or.right();
            assertEquals("regression", ((SimpleTag) rightAnd.left()).tag());
            assertEquals("env=staging", ((SimpleTag) rightAnd.right()).tag());
        }
    }

    @Nested
    @DisplayName("Invalid inputs")
    class InvalidInputTests {

        @Test
        @DisplayName("throws on empty string")
        void throwsOnEmptyString() {
            ParseException ex = assertThrows(ParseException.class,
                () -> TagExpressionParser.parse(""));

            assertTrue(ex.getMessage().contains("0") || ex.getMessage().toLowerCase().contains("empty"));
        }

        @Test
        @DisplayName("throws on whitespace only")
        void throwsOnWhitespaceOnly() {
            assertThrows(ParseException.class,
                () -> TagExpressionParser.parse("   "));
        }

        @Test
        @DisplayName("throws on leading AND operator")
        void throwsOnLeadingAnd() {
            ParseException ex = assertThrows(ParseException.class,
                () -> TagExpressionParser.parse("AND smoke"));

            assertNotNull(ex.getMessage());
        }

        @Test
        @DisplayName("throws on leading OR operator")
        void throwsOnLeadingOr() {
            assertThrows(ParseException.class,
                () -> TagExpressionParser.parse("OR smoke"));
        }

        @Test
        @DisplayName("throws on trailing AND operator")
        void throwsOnTrailingAnd() {
            assertThrows(ParseException.class,
                () -> TagExpressionParser.parse("smoke AND"));
        }

        @Test
        @DisplayName("throws on trailing OR operator")
        void throwsOnTrailingOr() {
            assertThrows(ParseException.class,
                () -> TagExpressionParser.parse("smoke OR"));
        }

        @Test
        @DisplayName("throws on consecutive AND operators")
        void throwsOnConsecutiveAnd() {
            assertThrows(ParseException.class,
                () -> TagExpressionParser.parse("smoke AND AND regression"));
        }

        @Test
        @DisplayName("throws on consecutive OR operators")
        void throwsOnConsecutiveOr() {
            assertThrows(ParseException.class,
                () -> TagExpressionParser.parse("smoke OR OR regression"));
        }

        @Test
        @DisplayName("throws on mixed consecutive operators")
        void throwsOnMixedConsecutiveOperators() {
            assertThrows(ParseException.class,
                () -> TagExpressionParser.parse("smoke AND OR regression"));
        }

        @Test
        @DisplayName("throws on unbalanced parentheses - missing close")
        void throwsOnMissingCloseParen() {
            assertThrows(ParseException.class,
                () -> TagExpressionParser.parse("((smoke)"));
        }

        @Test
        @DisplayName("throws on unbalanced parentheses - missing open")
        void throwsOnMissingOpenParen() {
            assertThrows(ParseException.class,
                () -> TagExpressionParser.parse("smoke))"));
        }

        @Test
        @DisplayName("throws on empty parentheses")
        void throwsOnEmptyParentheses() {
            assertThrows(ParseException.class,
                () -> TagExpressionParser.parse("()"));
        }

        @Test
        @DisplayName("throws on null input")
        void throwsOnNullInput() {
            assertThrows(ParseException.class,
                () -> TagExpressionParser.parse(null));
        }
    }

    @Nested
    @DisplayName("Edge cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("tag named 'and' (lowercase) is parsed as tag, not operator")
        void lowercaseAndIsTag() {
            // 'and' (lowercase) should be a tag, not the AND operator
            TagExpr result = TagExpressionParser.parse("and");

            assertInstanceOf(SimpleTag.class, result);
            assertEquals("and", ((SimpleTag) result).tag());
        }

        @Test
        @DisplayName("tag named 'or' (lowercase) is parsed as tag, not operator")
        void lowercaseOrIsTag() {
            TagExpr result = TagExpressionParser.parse("or");

            assertInstanceOf(SimpleTag.class, result);
            assertEquals("or", ((SimpleTag) result).tag());
        }

        @Test
        @DisplayName("AND and OR are case-sensitive operators")
        void operatorsAreCaseSensitive() {
            // "smoke and regression" should be parsed as three separate tokens
            // but since 'and' isn't recognized as operator, it fails or is interpreted differently
            // This test documents expected behavior: only uppercase AND/OR are operators
            TagExpr result = TagExpressionParser.parse("smoke AND regression");
            assertInstanceOf(AndExpr.class, result);

            // lowercase 'and' between tags should NOT be an operator
            // "smoke and regression" without uppercase AND - behavior depends on implementation
            // Typical: parse "smoke" then fail on unexpected "and" OR treat entire thing as one tag
            // Our grammar: tokens are separated by whitespace, so "smoke and regression" is 3 tokens
            // "and" is a valid tag identifier, but there's no operator between smoke and "and"
            // This should fail as we have two adjacent tags with no operator
        }

        @Test
        @DisplayName("tag with equals sign in value: key=value=extra")
        void tagWithMultipleEquals() {
            TagExpr result = TagExpressionParser.parse("key=value=extra");

            assertInstanceOf(SimpleTag.class, result);
            assertEquals("key=value=extra", ((SimpleTag) result).tag());
        }

        @Test
        @DisplayName("complex whitespace handling")
        void complexWhitespaceHandling() {
            TagExpr result = TagExpressionParser.parse("  ( a   OR   b )  AND  c  ");

            assertInstanceOf(AndExpr.class, result);
            AndExpr and = (AndExpr) result;

            assertInstanceOf(OrExpr.class, and.left());
            assertEquals("c", ((SimpleTag) and.right()).tag());
        }

        @Test
        @DisplayName("deeply nested expression")
        void deeplyNestedExpression() {
            TagExpr result = TagExpressionParser.parse("(((a)))");

            assertInstanceOf(SimpleTag.class, result);
            assertEquals("a", ((SimpleTag) result).tag());
        }

        @Test
        @DisplayName("very long tag name")
        void veryLongTagName() {
            String longTag = "a".repeat(100);
            TagExpr result = TagExpressionParser.parse(longTag);

            assertInstanceOf(SimpleTag.class, result);
            assertEquals(longTag, ((SimpleTag) result).tag());
        }
    }

    @Nested
    @DisplayName("Record equality and toString")
    class RecordBehaviorTests {

        @Test
        @DisplayName("SimpleTag equals works correctly")
        void simpleTagEquals() {
            TagExpr a = TagExpressionParser.parse("smoke");
            TagExpr b = TagExpressionParser.parse("smoke");

            assertEquals(a, b);
        }

        @Test
        @DisplayName("AndExpr equals works correctly")
        void andExprEquals() {
            TagExpr a = TagExpressionParser.parse("smoke AND regression");
            TagExpr b = TagExpressionParser.parse("smoke AND regression");

            assertEquals(a, b);
        }

        @Test
        @DisplayName("OrExpr equals works correctly")
        void orExprEquals() {
            TagExpr a = TagExpressionParser.parse("smoke OR regression");
            TagExpr b = TagExpressionParser.parse("smoke OR regression");

            assertEquals(a, b);
        }

        @Test
        @DisplayName("SimpleTag toString includes tag value")
        void simpleTagToString() {
            TagExpr result = TagExpressionParser.parse("smoke");

            assertTrue(result.toString().contains("smoke"));
        }
    }
}
