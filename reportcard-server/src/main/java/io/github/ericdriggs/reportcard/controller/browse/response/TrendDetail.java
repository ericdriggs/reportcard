package io.github.ericdriggs.reportcard.controller.browse.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Controls the detail level of the trend response")
public enum TrendDetail {
    @Schema(description = "Includes runStates and failureMessages per test case")
    full,
    @Schema(description = "Omits runStates and failureMessages for a compact overview")
    summary
}
