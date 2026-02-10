package io.github.ericdriggs.reportcard.persist.tags;

/**
 * Represents an OR expression in the tag query AST.
 *
 * <p>OR expressions require at least one operand to match.
 * OR has lower precedence than AND, so {@code a OR b AND c} parses as {@code a OR (b AND c)}.
 *
 * @param left the left operand
 * @param right the right operand
 */
public record OrExpr(TagExpr left, TagExpr right) implements TagExpr {
}
