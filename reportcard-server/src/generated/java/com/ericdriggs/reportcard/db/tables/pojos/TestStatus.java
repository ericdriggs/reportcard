/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db.tables.pojos;


import java.io.Serializable;

import javax.annotation.Generated;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "https://www.jooq.org",
        "jOOQ version:3.13.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestStatus implements Serializable {

    private static final long serialVersionUID = -1569762693;

    private Byte   testStatusId;
    private String testStatusName;

    public TestStatus() {}

    public TestStatus(TestStatus value) {
        this.testStatusId = value.testStatusId;
        this.testStatusName = value.testStatusName;
    }

    public TestStatus(
        Byte   testStatusId,
        String testStatusName
    ) {
        this.testStatusId = testStatusId;
        this.testStatusName = testStatusName;
    }

    public Byte getTestStatusId() {
        return this.testStatusId;
    }

    public TestStatus setTestStatusId(Byte testStatusId) {
        this.testStatusId = testStatusId;
        return this;
    }

    public String getTestStatusName() {
        return this.testStatusName;
    }

    public TestStatus setTestStatusName(String testStatusName) {
        this.testStatusName = testStatusName;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TestStatus (");

        sb.append(testStatusId);
        sb.append(", ").append(testStatusName);

        sb.append(")");
        return sb.toString();
    }
}
