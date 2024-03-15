package io.github.ericdriggs.reportcard.pojos;

import java.io.Serial;
import java.io.Serializable;

import lombok.Data;

@Data
public class FaultContext implements Serializable {

    @Serial
    private static final long serialVersionUID = 3737530249635128082L;

    private Byte   faultContextId;
    private String faultContextName;
}
