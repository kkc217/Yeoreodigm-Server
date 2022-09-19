package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@SequenceGenerator(
        name = "PLACE_ID_SEQ_GENERATOR",
        sequenceName = "place_id_seq",
        allocationSize = 1)
public class Places {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PLACE_ID_SEQ_GENERATOR"
    )
    @Column(name = "place_id")
    private Long id;

    private String title;

    private String address;

    private String introduction;

    private double latitude;

    private double longitude;

    private String dial_num;

    private String type;

    private String imageUrl;

    private int score;

    private String tag;

    private short children;

    private short pet;

}
