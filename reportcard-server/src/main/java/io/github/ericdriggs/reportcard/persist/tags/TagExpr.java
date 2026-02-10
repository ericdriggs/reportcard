package io.github.ericdriggs.reportcard.persist.tags;

/**
 * Sealed interface for tag expression AST nodes.
 *
 * <p>The tag expression grammar supports boolean expressions with AND, OR, and parentheses:
 * <pre>
 * expr     := term (OR term)*
 * term     := factor (AND factor)*
 * factor   := TAG | ( expr )
 * TAG      := identifier | identifier=value
 * </pre>
 *
 * <p>Precedence: AND binds tighter than OR (standard boolean algebra).
 *
 * @see TagExpressionParser
 * @see SimpleTag
 * @see AndExpr
 * @see OrExpr
 */
public sealed interface TagExpr permits SimpleTag, AndExpr, OrExpr {
}
