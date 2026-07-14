package io.github.ericdriggs.reportcard.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JunitControllerPhaseHelperTest {

    @Test
    void runPhaseNonBlocking_phaseThrows_isCaughtAndLogged_doesNotPropagate() {
        assertDoesNotThrow(() -> JunitController.runPhaseNonBlocking("synthetic phase",
                () -> { throw new RuntimeException("synthetic failure"); }));
    }

    @Test
    void runPhaseNonBlocking_phaseSucceeds_runs() {
        boolean[] ran = {false};
        JunitController.runPhaseNonBlocking("synthetic phase", () -> ran[0] = true);
        assertTrue(ran[0], "phase must actually run when it doesn't throw");
    }

    @Test
    void runPhaseNonBlocking_onePhaseThrows_doesNotPreventAnotherPhaseFromRunning() {
        boolean[] secondPhaseRan = {false};

        JunitController.runPhaseNonBlocking("first synthetic phase", () -> {
            throw new RuntimeException("synthetic failure in first phase");
        });
        JunitController.runPhaseNonBlocking("second synthetic phase", () -> secondPhaseRan[0] = true);

        assertTrue(secondPhaseRan[0], "a phase that throws must not prevent a later phase from running");
    }
}
