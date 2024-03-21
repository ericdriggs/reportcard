package io.github.ericdriggs.reportcard.model;

import lombok.Data;

@Data
public class TestCaseFaultModel extends io.github.ericdriggs.reportcard.dto.TestCaseFault {

    private FaultContext faultContext;

    public FaultContext getFaultContext() {
        if (faultContext == null && getFaultContextFk() != null) {
            setFaultContextFk(getFaultContextFk());
        }
        return faultContext;
    }

    public TestCaseFaultModel setFaultContextFk(Byte faultContextFk) {
        this.faultContext = FaultContext.fromFaultContextId(faultContextFk);
        this.faultContextFk = faultContextFk;
        return this;
    }

}
