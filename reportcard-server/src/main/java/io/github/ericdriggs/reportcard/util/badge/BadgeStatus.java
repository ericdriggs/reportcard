package io.github.ericdriggs.reportcard.util.badge;

import io.github.ericdriggs.reportcard.model.graph.StageGraph;
import io.github.ericdriggs.reportcard.model.graph.TestResultGraph;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

@Slf4j
public enum BadgeStatus {
    PASS("#4c1"),
    FAIL("#f43"),
    SKIP("#cb3"),
    UNKNOWN("#f40"),
    LAST_SUCCESS("#454");

    private final String text;
    private final String color;

    BadgeStatus(String color) {
        this.color = color;
        this.text = StringUtils.capitalize(this.name().toLowerCase());
    }

    public String getColor() {
        return color;
    }

    public String getText() {
        return text;
    }

    public static BadgeStatus fromIsSuccess(boolean isSuccess) {
        return isSuccess ? BadgeStatus.PASS : BadgeStatus.FAIL;
    }

    public static BadgeStatus fromStageGraph(StageGraph stageGraph) {
        if (stageGraph == null || CollectionUtils.isEmpty(stageGraph.testResults())) {
            return BadgeStatus.UNKNOWN;
        }

        for (TestResultGraph testResultGraph : stageGraph.testResults()) {
            if (!testResultGraph.isSuccess()) {
                return BadgeStatus.FAIL;
            }
        }
        return BadgeStatus.PASS;
    }

}
