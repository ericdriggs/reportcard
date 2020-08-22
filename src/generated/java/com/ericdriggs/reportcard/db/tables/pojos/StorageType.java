/*
 * This file is generated by jOOQ.
 */
package com.ericdriggs.reportcard.db.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StorageType implements Serializable {

    private static final long serialVersionUID = 724312717;

    private final Byte   id;
    private final String name;

    public StorageType(StorageType value) {
        this.id = value.id;
        this.name = value.name;
    }

    public StorageType(
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
        StringBuilder sb = new StringBuilder("StorageType (");

        sb.append(id);
        sb.append(", ").append(name);

        sb.append(")");
        return sb.toString();
    }
}
