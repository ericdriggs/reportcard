package com.ericdriggs.ragnarok.model;

import com.ericdriggs.ragnarok.interfaces.HasNameId;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * A Github repository.
 * Belongs to an Org
 */
@Data
@Entity
public class Repo implements HasNameId {
    private String name;
    @Id
    private Long id;
    private Long orgFk;
}
