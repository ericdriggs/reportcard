package io.github.ericdriggs.reportcard.controller.browse;

import io.github.ericdriggs.reportcard.persist.BrowseService;
import io.github.ericdriggs.reportcard.persist.browse.AbstractBrowseServiceTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

public class BrowseJsonControllerJobInfoValidationTest extends AbstractBrowseServiceTest {

    private final BrowseJsonController controller;

    @Autowired
    public BrowseJsonControllerJobInfoValidationTest(BrowseService browseService, BrowseJsonController controller) {
        super(browseService);
        this.controller = controller;
    }

    @Test
    void malformedJobInfo_colonSeparated_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                controller.getRepoDashboardWithJobInfoJson(
                        "myrepo", "application:foo",
                        java.util.Collections.emptyList(), true, null));
    }

    @Test
    void malformedJobInfo_noEqualsSign_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                controller.getRepoDashboardWithJobInfoJson(
                        "myrepo", "justAString",
                        java.util.Collections.emptyList(), true, null));
    }

    @Test
    void malformedJobInfo_flatEndpoint_throws() {
        assertThrows(IllegalArgumentException.class, () ->
                controller.getRepoDashboardFlatWithJobInfoJson(
                        "myrepo", "application:foo",
                        java.util.Collections.emptyList(), true, null));
    }

    @Test
    void validJobInfo_doesNotThrow() {
        assertDoesNotThrow(() ->
                controller.getRepoDashboardWithJobInfoJson(
                        "myrepo", "application=foo",
                        java.util.Collections.emptyList(), true, null));
    }

    @Test
    void validateJobInfo_emptyString_doesNotThrow() {
        assertDoesNotThrow(() -> controller.validateJobInfo(""));
    }

    @Test
    void validateJobInfo_nullString_doesNotThrow() {
        assertDoesNotThrow(() -> controller.validateJobInfo(null));
    }
}
