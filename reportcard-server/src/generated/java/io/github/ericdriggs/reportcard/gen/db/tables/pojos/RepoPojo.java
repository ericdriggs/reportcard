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
public class RepoPojo implements Serializable {

    private static final long serialVersionUID = -1642182918;

    private Integer repoId;
    private String  repoName;
    private Integer orgFk;
}
