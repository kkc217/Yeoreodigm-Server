package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SequenceGenerator(
        name = "PLACE_COMMENT_ID_SEQ_GENERATOR",
        sequenceName = "place_comment_id_seq",
        allocationSize = 1)
public class PlaceComment {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PLACE_COMMENT_ID_SEQ_GENERATOR"
    )
    @Column(name = "place_comment_id")
    private Long id;

    private Long placeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String text;

    private LocalDateTime created;

    private LocalDateTime modified;

    public PlaceComment(Long placeId, Member member, String text) {
        this.placeId = placeId;
        this.member = member;
        this.text = text;
        this.created = LocalDateTime.now();
        this.modified = created;
    }

}
