package com.ericdriggs.ragnarok.model;

import com.ericdriggs.ragnarok.interfaces.HasNameId;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seq_repo")
    private Long id;
    private Long orgFk;
}
