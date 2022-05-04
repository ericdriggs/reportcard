/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.pojos;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TestCase implements Serializable {

    private static final long serialVersionUID = -3938894842199056311L;

    private Long testCaseId;
    private Long testSuiteFk;
    private String name;
    private String className;
    private BigDecimal time;
    private Byte testStatusFk;
}
