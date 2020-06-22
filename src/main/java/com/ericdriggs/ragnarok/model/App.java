package com.ericdriggs.ragnarok.model;

import com.ericdriggs.ragnarok.interfaces.HasNameId;
import lombok.Data;

import javax.persistence.*;

/**
 * An application.
 * Belongs to a branch.
 * Used to support multi-project repositories and kuberenetes multiple piplines.
 * For single-project repositories, App.name == Repo.name
 */
@Data
@Entity
public class App implements HasNameId {
    private String name;
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seq_app")
    private Long id;
    @ManyToOne
    private Branch branch;
}
