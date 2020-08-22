//package com.ericdriggs.reportcard.model;
//
//import com.ericdriggs.reportcard.interfaces.HasNameId;
//import lombok.Data;
//
//import javax.persistence.*;
//
///**
// * A Github repository.
// * Belongs to an Org
// */
//@Data
//@Entity
//public class Repo implements HasNameId {
//    private String name;
//    @Id
//    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seq_repo")
//    private Long id;
//    @ManyToOne
//    private Org org;
//}