package com.yeoreodigm.server.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
@SequenceGenerator(
        name = "HASH_TAG_ID_SEQ_GENERATOR",
        sequenceName = "hash_tag_id_seq",
        allocationSize = 1)
public class HashTag {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "HASH_TAG_ID_SEQ_GENERATOR")
    @Column(name = "hash_tag_id")
    private Long id;

    private String content;

}
