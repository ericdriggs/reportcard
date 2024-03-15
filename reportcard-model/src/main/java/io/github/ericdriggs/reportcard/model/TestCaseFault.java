package io.github.ericdriggs.reportcard.model;

public class TestCaseFault extends io.github.ericdriggs.reportcard.pojos.TestCaseFault {

    private FaultContext faultContext;

    public FaultContext getFaultContext() {
        if (faultContext == null && getFaultContextFk() != null) {
            setFaultContextFk(getFaultContextFk());
        }
        return faultContext;
    }

    public TestCaseFault setFaultContextFk(Byte faultContextFk) {
        this.faultContext = FaultContext.fromFaultContextId(faultContextFk);
        this.faultContextFk = faultContextFk;
        return this;
    }

}
