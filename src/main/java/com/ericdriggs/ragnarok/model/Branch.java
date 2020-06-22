package com.ericdriggs.ragnarok.model;

import com.ericdriggs.ragnarok.interfaces.HasNameId;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * A Github branch.
 * Belongs to a repo.
 */
@Data
@Entity
public class Branch implements HasNameId {
    private String name;
    @Id
    private Long id;
    private Long repoFk;
}
