package io.github.ericdriggs.reportcard.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.ericdriggs.reportcard.util.truncate.TruncateUtils;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TestCaseFaultModel extends io.github.ericdriggs.reportcard.dto.TestCaseFault {

    public TestCaseFaultModel() {
    }

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

    public TestCaseFaultModel withTruncateErrorMessages() {
        return this.toBuilder()
                .message(truncateString(message))
                .value(truncateString(value))
                .build();
    }

    public static List<TestCaseFaultModel> withTruncatedErrorMessages(List<TestCaseFaultModel> fs) {
        if (fs == null || fs.isEmpty()) {
            return fs;
        }
        List<TestCaseFaultModel> faults = new ArrayList<>();
        for (TestCaseFaultModel f : fs) {
            faults.add(f.withTruncateErrorMessages());
        }
        return faults;
    }


    static String truncateString(String str) {
        return TruncateUtils.truncateString(str, truncateLength);
    }

    private final static int truncateLength = 512;


}
