package io.github.ericdriggs.reportcard.model.graph.comparator;

import io.github.ericdriggs.reportcard.model.graph.RunGraph;
import io.github.ericdriggs.reportcard.util.CompareUtil;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serial;
import java.util.Comparator;

public enum GraphComparators {
    ;//static comparators
    public static final Comparator<RunGraph> RUN_GRAPH_DESC
            = new GraphComparators.RunGraphDescendingComparator();

    private static class RunGraphDescendingComparator
            implements Comparator<RunGraph>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = -6299948196899882312L;

        @Override
        public int compare(RunGraph o1, RunGraph o2) {
            //reverse order for descending
            return RunGraphComparator.doCompare(o2, o1);
        }
    }

    private static class RunGraphComparator
            implements Comparator<RunGraph>, java.io.Serializable {
        @Serial
        private static final long serialVersionUID = -6299948196899882312L;

        @Override
        public int compare(RunGraph o1, RunGraph o2) {
            return doCompare(o1, o2);
        }

        static int doCompare(RunGraph o1, RunGraph o2) {
            return CompareUtil.chainCompare(
                    ObjectUtils.compare(o1.runId(), o2.runId()),
                    ObjectUtils.compare(o1.runDate(), o2.runDate()),
                    ObjectUtils.compare(o1.sha(), o2.sha()),
                    ObjectUtils.compare(o1.runReference(), o2.runReference())
            );
        }

    }
}
