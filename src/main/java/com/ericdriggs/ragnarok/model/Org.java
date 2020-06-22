package com.ericdriggs.ragnarok.model;

import com.ericdriggs.ragnarok.interfaces.HasNameId;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * A Github organization
 */
@Data
@Entity
public class Org implements HasNameId {
    private String name;
    @Id
    private Long id;
}
