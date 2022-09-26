package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@SequenceGenerator(
        name = "MEMBER_PICTURE_ID_SEQ_GENERATOR",
        sequenceName = "member_picture_id_seq",
        allocationSize = 1)
public class MemberPicture {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "MEMBER_PICTURE_ID_SEQ_GENERATOR")
    @Column(name = "member_picture_id")
    private Long id;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

}
