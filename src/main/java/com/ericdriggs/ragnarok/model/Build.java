package com.ericdriggs.ragnarok.model;

import com.ericdriggs.ragnarok.interfaces.HasId;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class Build implements HasId
{
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator="seq_build")
    private Long id;
    private long appFk;
}
