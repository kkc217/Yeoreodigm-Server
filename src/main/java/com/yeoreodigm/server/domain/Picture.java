package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@SequenceGenerator(
        name = "PICTURE_ID_SEQ_GENERATOR",
        sequenceName = "member_picture_id_seq",
        allocationSize = 1)
public class Picture {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PICTURE_ID_SEQ_GENERATOR")
    @Column(name = "picture_id")
    private Long id;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

}
