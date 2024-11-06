package io.github.ericdriggs.reportcard.util.badge;

import io.github.ericdriggs.reportcard.model.graph.RunGraph;
import io.github.ericdriggs.reportcard.model.graph.StageGraph;
import io.github.ericdriggs.reportcard.model.graph.TestResultGraph;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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

    public static BadgeStatus fromCollection(Collection<BadgeStatus> badgeStatuses) {
        if ((badgeStatuses == null) || badgeStatuses.isEmpty()) {
            return BadgeStatus.UNKNOWN;
        }
        BadgeStatus ret = BadgeStatus.UNKNOWN;
        for (BadgeStatus badgeStatus : badgeStatuses) {
            if (badgeStatus == BadgeStatus.FAIL) {
                return badgeStatus;
            }
            else if (badgeStatus == BadgeStatus.PASS) {
                ret = badgeStatus;
            }
            else if (badgeStatus == BadgeStatus.SKIP && ret != BadgeStatus.PASS) {
                ret = badgeStatus;
            }
        }
        return ret;
    }

    public static BadgeStatus fromRunGraph(RunGraph runGraph) {
        if (runGraph == null || CollectionUtils.isEmpty(runGraph.stages())) {
            return BadgeStatus.UNKNOWN;
        }

        List<BadgeStatus> stageBadges = new ArrayList<>();
        for (StageGraph stageGraph : runGraph.stages()) {
            stageBadges.add(fromStageGraph(stageGraph));
        }
        return fromCollection(stageBadges);
    }

    public static BadgeStatus fromStageGraph(StageGraph stageGraph) {
        if (stageGraph == null || CollectionUtils.isEmpty(stageGraph.testResults())) {
            return BadgeStatus.UNKNOWN;
        }

        BadgeStatus ret = BadgeStatus.UNKNOWN;
        for (TestResultGraph testResultGraph : stageGraph.testResults()) {
            if (testResultGraph.isSuccess()) {
                ret = BadgeStatus.PASS;
            } else {
                if (testResultGraph.tests() > testResultGraph.skipped()) {
                    return BadgeStatus.FAIL;
                } else if (ret == BadgeStatus.UNKNOWN) {
                    ret = BadgeStatus.SKIP;
                }
            }
        }
        return ret;
    }

    static TreeMap<String, BadgeStatus> textStatusMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

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
