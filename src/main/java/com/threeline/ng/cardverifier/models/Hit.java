package com.threeline.ng.cardverifier.models;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
public class Hit {
    @Id
    @GeneratedValue
    private long id;
    @Column(unique = true)
    private Integer cardId;
    private Integer hitCount;
    private String scheme;
    private String type;
    private String bank;
}
