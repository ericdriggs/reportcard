//package com.ericdriggs.reportcard.model;
//
//import com.ericdriggs.reportcard.interfaces.HasId;
//import lombok.Data;
//
//import javax.persistence.*;
//
//@Data
//@Entity
//public class BuildStage implements HasId
//{
//    @Id
//    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seq_build_stage")
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "id")
//    Build build;
//
//    @ManyToOne
//    @JoinColumn(name = "id")
//    Stage stage;
//}