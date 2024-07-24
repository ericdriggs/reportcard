package io.github.ericdriggs.reportcard.util.badge;

import io.github.ericdriggs.reportcard.model.graph.StageGraph;
import io.github.ericdriggs.reportcard.model.graph.TestResultGraph;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.TreeMap;

@Slf4j
public enum BadgeStatus {
    PASS("#4c1", "Pass"),
    FAIL("#f43", "Fail"),
    SKIP("#cb3", "Skip"),
    UNKNOWN("#e80", "???"),
    LAST_SUCCESS("#454", "Last Success");

    private final String color;
    private final String text;

    BadgeStatus(String color, String text) {
        this.color = color;
        this.text = text;
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

    static TreeMap<String, BadgeStatus> textStatusMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);

    static {
        for (BadgeStatus badgeStatus : BadgeStatus.values()) {
            textStatusMap.put(badgeStatus.text, badgeStatus);
        }
    }

    public static BadgeStatus fromText(String text) {
        final BadgeStatus badgeStatus = textStatusMap.get(text);
        if (badgeStatus == null) {
            return BadgeStatus.UNKNOWN;
        }
        return badgeStatus;
    }

}
