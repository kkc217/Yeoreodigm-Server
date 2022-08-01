package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
public class Places {

    @Id
    @GeneratedValue
    @Column(name = "place_id")
    private Long id;

    private String title;

    private String address;

    private String introduction;

    private float latitude;

    private float longitude;

    private String dial_num;

    private String type;

    private String imageUrl;

    private int score;

    private String tag;

}
