package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class MapMarker {

    @Id @GeneratedValue
    private int id;

    private int day;

    private String color;

}
