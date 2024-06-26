/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.dto;


import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TestCase implements Serializable {

    @Serial
    private static final long serialVersionUID = -3938894842199056311L;

    protected Long testCaseId;
    protected Long testSuiteFk;
    protected String name;
    protected String className;
    protected BigDecimal time;
    protected Byte testStatusFk;
    protected String assertions;
    protected String systemOut;
    protected String systemErr;
}
