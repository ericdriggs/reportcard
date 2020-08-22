//package com.ericdriggs.reportcard.model;
//
//import com.ericdriggs.reportcard.interfaces.HasNameId;
//import lombok.Data;
//
//import javax.persistence.*;
//
///**
// * A Github branch.
// * Belongs to a repo.
// */
//@Data
//@Entity
//public class Branch implements HasNameId {
//    private String name;
//    @Id
//    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seq_branch")
//    private Long id;
//    @ManyToOne
//    private Repo repo;
//}
