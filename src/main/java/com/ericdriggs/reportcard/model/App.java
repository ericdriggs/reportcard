//package com.ericdriggs.reportcard.model;
//
//import com.ericdriggs.reportcard.interfaces.HasNameId;
//import lombok.Data;
//
//import javax.persistence.*;
//
///**
// * An application.
// * Belongs to a branch.
// * Used to support multi-project repositories and kuberenetes multiple pipelines.
// * For single-project repositories, App.name == Repo.name
// */
//@Data
//@Entity
//public class App implements HasNameId {
//    private String name;
//    @Id
//    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seq_app")
//    private Long id;
//    @ManyToOne
//    private Branch branch;
//}
