package io.github.ericdriggs.reportcard.persist.tags;

/**
 * Represents an AND expression in the tag query AST.
 *
 * <p>AND expressions require both operands to match.
 * AND binds tighter than OR, so {@code a OR b AND c} parses as {@code a OR (b AND c)}.
 *
 * @param left the left operand
 * @param right the right operand
 */
public record AndExpr(TagExpr left, TagExpr right) implements TagExpr {
}
