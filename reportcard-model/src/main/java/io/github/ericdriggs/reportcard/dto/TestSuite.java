/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TestSuite implements Serializable {

    @Serial
    private static final long serialVersionUID = -200588524670176256L;

    private Long testSuiteId;
    private Long testResultFk;
    private String name;
    private Integer tests;
    private Integer skipped;
    private Integer error;
    private Integer failure;
    private BigDecimal time;
    private String packageName;
    private String group;
    private String properties;
    private Boolean isSuccess;
    private Boolean hasSkip;
    private String systemOut;
    private String systemErr;

}
