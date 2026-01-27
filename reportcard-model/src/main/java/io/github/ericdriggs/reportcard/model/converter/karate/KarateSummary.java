package io.github.ericdriggs.reportcard.model.converter.karate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * POJO for deserializing Karate's karate-summary-json.txt file.
 * Contains timing data (elapsedTime, resultDate) used to calculate run start/end times.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KarateSummary {

    @JsonProperty("elapsedTime")
    private Double elapsedTime;  // milliseconds

    @JsonProperty("totalTime")
    private Double totalTime;

    @JsonProperty("resultDate")
    private String resultDate;  // "2026-01-20 03:00:56 PM"

    @JsonProperty("featuresPassed")
    private Integer featuresPassed;

    @JsonProperty("featuresFailed")
    private Integer featuresFailed;

    @JsonProperty("scenariosPassed")
    private Integer scenariosPassed;

    @JsonProperty("scenariosfailed")  // Note: lowercase 'f' in actual Karate JSON
    private Integer scenariosFailed;

    @JsonProperty("version")
    private String version;

    @JsonProperty("efficiency")
    private Double efficiency;

    @JsonProperty("threads")
    private Integer threads;

    @JsonProperty("featuresSkipped")
    private Integer featuresSkipped;
}
