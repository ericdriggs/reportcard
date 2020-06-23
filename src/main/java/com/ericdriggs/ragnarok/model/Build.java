package com.ericdriggs.ragnarok.model;

import com.ericdriggs.ragnarok.interfaces.HasId;
import lombok.Data;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
public class Build implements HasId
{
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seq_build")
    private Long id;
    @ManyToOne
    private App app;


//    @Column(name = "json_input", columnDefinition = "json")
    @Column(    columnDefinition = "json")
    private String metaDataJson;
//    @ElementCollection
//    @MapKeyColumn(name = "key")
//    @Column(name = "value")
//    @CollectionTable(name="build_metadata")
//    private Sgr]] buildMetaDataMap = new HashMap<String, String>();
}
