package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestCaseFaultModel extends io.github.ericdriggs.reportcard.dto.TestCaseFault {

    private FaultContext faultContext;

    @JsonProperty("faultContext")
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
