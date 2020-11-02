package com.atkuzmanov.genesys.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class TimestampEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String timestampAsString;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTimestampAsString() {
        return timestampAsString;
    }

    public void setTimestampAsString(String timestampAsString) {
        this.timestampAsString = timestampAsString;
    }
}
