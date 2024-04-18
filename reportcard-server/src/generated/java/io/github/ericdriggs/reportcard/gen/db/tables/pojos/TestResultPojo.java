/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.pojos;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated
@lombok.AllArgsConstructor
@lombok.Data
@lombok.experimental.SuperBuilder(toBuilder = true)
@lombok.NoArgsConstructor
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestResultPojo implements Serializable {

    private static final long serialVersionUID = -783359589;

    private Long testResultId;
    private Long stageFk;
    private Integer tests;
    private Integer skipped;
    private Integer error;
    private Integer failure;
    private BigDecimal time;
    private LocalDateTime testResultCreated;
    private String externalLinks;
    private Boolean isSuccess;
    private Boolean hasSkip;
}
