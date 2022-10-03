package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@SequenceGenerator(
        name = "FRAME_ID_SEQ_GENERATOR",
        sequenceName = "frame_id_seq",
        allocationSize = 1)
public class Frame {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "FRAME_ID_SEQ_GENERATOR")
    @Column(name = "frame_id")
    private Long id;

    private String title;

    private String address;

    @Column(name = "x_size")
    private int sizeX;

    @Column(name = "y_size")
    private int sizeY;

}
