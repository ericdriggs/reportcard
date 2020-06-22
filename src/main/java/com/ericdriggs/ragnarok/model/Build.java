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

    @ElementCollection
    @MapKeyColumn(name = "key")
//    @Column(name = "value")
//    @CollectionTable(name="build_metadata")
    private Map<String, String> buildMetaDataMap = new HashMap<String, String>();
}
