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
@lombok.AllArgsConstructor
@lombok.Data
@lombok.experimental.SuperBuilder(toBuilder = true)
@lombok.NoArgsConstructor
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class FaultContextPojo implements Serializable {

    private static final long serialVersionUID = -125137178;

    private Byte   faultContextId;
    private String faultContextName;
}
