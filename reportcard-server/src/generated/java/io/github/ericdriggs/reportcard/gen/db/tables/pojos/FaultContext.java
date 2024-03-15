/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.pojos;


import java.io.Serializable;

import lombok.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class FaultContext implements Serializable {

    private static final long serialVersionUID = -625465880;

    private Byte   faultContextId;
    private String faultContextName;

    public FaultContext() {}

    public FaultContext(FaultContext value) {
        this.faultContextId = value.faultContextId;
        this.faultContextName = value.faultContextName;
    }

    public FaultContext(
        Byte   faultContextId,
        String faultContextName
    ) {
        this.faultContextId = faultContextId;
        this.faultContextName = faultContextName;
    }

    /**
     * Getter for <code>reportcard.fault_context.fault_context_id</code>.
     */
    public Byte getFaultContextId() {
        return this.faultContextId;
    }

    /**
     * Setter for <code>reportcard.fault_context.fault_context_id</code>.
     */
    public FaultContext setFaultContextId(Byte faultContextId) {
        this.faultContextId = faultContextId;
        return this;
    }

    /**
     * Getter for <code>reportcard.fault_context.fault_context_name</code>.
     */
    public String getFaultContextName() {
        return this.faultContextName;
    }

    /**
     * Setter for <code>reportcard.fault_context.fault_context_name</code>.
     */
    public FaultContext setFaultContextName(String faultContextName) {
        this.faultContextName = faultContextName;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("FaultContext (");

        sb.append(faultContextId);
        sb.append(", ").append(faultContextName);

        sb.append(")");
        return sb.toString();
    }
}
