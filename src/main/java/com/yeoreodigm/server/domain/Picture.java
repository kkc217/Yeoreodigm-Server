package com.yeoreodigm.server.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@SequenceGenerator(
        name = "PICTURE_ID_SEQ_GENERATOR",
        sequenceName = "picture_id_seq",
        allocationSize = 1)
@NoArgsConstructor
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

    public Picture(String address, Member member) {
        this.address = address;
        this.member = member;
    }

}
