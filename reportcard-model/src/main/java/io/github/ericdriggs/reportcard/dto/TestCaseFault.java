
package io.github.ericdriggs.reportcard.dto;

import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@Data
@SuperBuilder(builderMethodName = "testCaseFaultBuilder", toBuilder = true)
public class TestCaseFault implements Serializable {

    public TestCaseFault() {
    }

    @Serial
    private static final long serialVersionUID = 2927190104070231917L;

    protected Long testCaseFaultId;
    protected Long testCaseFk;
    protected Byte faultContextFk;
    protected String type;
    protected String message;
    protected String value;
}
