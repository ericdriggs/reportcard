package io.github.ericdriggs.reportcard.persist.tags;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TagExprEvaluator.
 */
class TagExprEvaluatorTest {

    @Test
    void matches_simpleTag_returnsTrue() {
        TagExpr expr = TagExpressionParser.parse("smoke");
        assertTrue(TagExprEvaluator.matches(expr, Set.of("smoke", "regression")));
    }

    @Test
    void matches_simpleTag_returnsFalse() {
        TagExpr expr = TagExpressionParser.parse("smoke");
        assertFalse(TagExprEvaluator.matches(expr, Set.of("regression", "integration")));
    }

    @Test
    void matches_keyValueTag_returnsTrue() {
        TagExpr expr = TagExpressionParser.parse("env=prod");
        assertTrue(TagExprEvaluator.matches(expr, Set.of("env=prod", "priority=high")));
    }

    @Test
    void matches_keyValueTag_returnsFalse() {
        TagExpr expr = TagExpressionParser.parse("env=prod");
        assertFalse(TagExprEvaluator.matches(expr, Set.of("env=staging", "priority=high")));
    }

    @Test
    void matches_andExpression_bothPresent_returnsTrue() {
        TagExpr expr = TagExpressionParser.parse("smoke AND regression");
        assertTrue(TagExprEvaluator.matches(expr, Set.of("smoke", "regression", "integration")));
    }

    @Test
    void matches_andExpression_onlyOnePresent_returnsFalse() {
        TagExpr expr = TagExpressionParser.parse("smoke AND regression");
        assertFalse(TagExprEvaluator.matches(expr, Set.of("smoke", "integration")));
    }

    @Test
    void matches_orExpression_onePresent_returnsTrue() {
        TagExpr expr = TagExpressionParser.parse("smoke OR regression");
        assertTrue(TagExprEvaluator.matches(expr, Set.of("smoke", "integration")));
    }

    @Test
    void matches_orExpression_neitherPresent_returnsFalse() {
        TagExpr expr = TagExpressionParser.parse("smoke OR regression");
        assertFalse(TagExprEvaluator.matches(expr, Set.of("integration", "unit")));
    }

    @Test
    void matches_complexExpression_returnsCorrectly() {
        // (smoke AND env=prod) OR regression
        TagExpr expr = TagExpressionParser.parse("(smoke AND env=prod) OR regression");

        // Has both smoke and env=prod - should match
        assertTrue(TagExprEvaluator.matches(expr, Set.of("smoke", "env=prod")));

        // Has regression only - should match
        assertTrue(TagExprEvaluator.matches(expr, Set.of("regression")));

        // Has smoke but not env=prod, no regression - should not match
        assertFalse(TagExprEvaluator.matches(expr, Set.of("smoke", "env=staging")));
    }

    @Test
    void matches_nullExpr_returnsFalse() {
        assertFalse(TagExprEvaluator.matches(null, Set.of("smoke")));
    }

    @Test
    void matches_nullTags_returnsFalse() {
        TagExpr expr = TagExpressionParser.parse("smoke");
        assertFalse(TagExprEvaluator.matches(expr, null));
    }

    @Test
    void matches_emptyTags_returnsFalse() {
        TagExpr expr = TagExpressionParser.parse("smoke");
        assertFalse(TagExprEvaluator.matches(expr, List.of()));
    }
}
