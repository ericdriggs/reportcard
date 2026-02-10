package io.github.ericdriggs.reportcard.persist.tags;

/**
 * Represents a simple tag expression (leaf node in the AST).
 *
 * <p>A simple tag can be:
 * <ul>
 *   <li>An identifier: {@code smoke}, {@code regression}, {@code smoke-test}</li>
 *   <li>A key=value pair: {@code env=prod}, {@code priority=high}</li>
 * </ul>
 *
 * <p>The tag value is stored as-is, including the equals sign for key=value tags.
 *
 * @param tag the tag value (identifier or key=value)
 */
public record SimpleTag(String tag) implements TagExpr {
}
