
package io.github.ericdriggs.reportcard.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TestCaseFault implements Serializable {

    protected static final long serialVersionUID = 2927190104070231917L;

    protected Long   testCaseFaultId;
    protected Long   testCaseFk;
    protected Byte   faultContextFk;
    protected String type;
    protected String message;
    protected String value;
}
