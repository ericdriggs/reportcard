/*
 * This file is generated by jOOQ.
 */
package io.github.ericdriggs.reportcard.gen.db.tables.pojos;


import java.io.Serializable;

import javax.annotation.processing.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.14.9"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Execution implements Serializable {

    private static final long serialVersionUID = 1941611725;

    private Long   executionId;
    private String executionReference;
    private Long   contextFk;

    public Execution() {}

    public Execution(Execution value) {
        this.executionId = value.executionId;
        this.executionReference = value.executionReference;
        this.contextFk = value.contextFk;
    }

    public Execution(
        Long   executionId,
        String executionReference,
        Long   contextFk
    ) {
        this.executionId = executionId;
        this.executionReference = executionReference;
        this.contextFk = contextFk;
    }

    /**
     * Getter for <code>reportcard.execution.execution_id</code>.
     */
    public Long getExecutionId() {
        return this.executionId;
    }

    /**
     * Setter for <code>reportcard.execution.execution_id</code>.
     */
    public Execution setExecutionId(Long executionId) {
        this.executionId = executionId;
        return this;
    }

    /**
     * Getter for <code>reportcard.execution.execution_reference</code>.
     */
    public String getExecutionReference() {
        return this.executionReference;
    }

    /**
     * Setter for <code>reportcard.execution.execution_reference</code>.
     */
    public Execution setExecutionReference(String executionReference) {
        this.executionReference = executionReference;
        return this;
    }

    /**
     * Getter for <code>reportcard.execution.context_fk</code>.
     */
    public Long getContextFk() {
        return this.contextFk;
    }

    /**
     * Setter for <code>reportcard.execution.context_fk</code>.
     */
    public Execution setContextFk(Long contextFk) {
        this.contextFk = contextFk;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Execution (");

        sb.append(executionId);
        sb.append(", ").append(executionReference);
        sb.append(", ").append(contextFk);

        sb.append(")");
        return sb.toString();
    }
}
