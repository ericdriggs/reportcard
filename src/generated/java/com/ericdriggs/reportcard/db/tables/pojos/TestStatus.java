/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class TestStatus implements Serializable {

    private static final long serialVersionUID = -326666010;

    private final Byte   id;
    private final String name;

    public TestStatus(TestStatus value) {
        this.id = value.id;
        this.name = value.name;
    }

    public TestStatus(
        Byte   id,
        String name
    ) {
        this.id = id;
        this.name = name;
    }

    public Byte getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("TestStatus (");

        sb.append(id);
        sb.append(", ").append(name);

        sb.append(")");
        return sb.toString();
    }
}
