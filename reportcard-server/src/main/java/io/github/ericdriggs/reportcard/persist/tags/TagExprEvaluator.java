package io.github.ericdriggs.reportcard.persist.tags;

import java.util.Collection;
import java.util.Set;

/**
 * Evaluates tag expressions against a collection of tags.
 */
public class TagExprEvaluator {

    /**
     * Evaluates whether a tag expression matches a collection of tags.
     *
     * @param expr the tag expression to evaluate
     * @param tags the tags to match against
     * @return true if the expression matches the tags
     */
    public static boolean matches(TagExpr expr, Collection<String> tags) {
        if (expr == null || tags == null || tags.isEmpty()) {
            return false;
        }
        return evaluate(expr, Set.copyOf(tags));
    }

    private static boolean evaluate(TagExpr expr, Set<String> tags) {
        if (expr instanceof SimpleTag simple) {
            return tags.contains(simple.tag());
        } else if (expr instanceof AndExpr and) {
            return evaluate(and.left(), tags) && evaluate(and.right(), tags);
        } else if (expr instanceof OrExpr or) {
            return evaluate(or.left(), tags) || evaluate(or.right(), tags);
        }
        return false;
    }
}
