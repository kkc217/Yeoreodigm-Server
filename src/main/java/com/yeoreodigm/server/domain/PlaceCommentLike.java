package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "multiIndex1", columnList = "placeCommentId, memberId"))
@SequenceGenerator(
        name = "PLACE_COMMENT_LIKE_ID_SEQ_GENERATOR",
        sequenceName = "place_comment_like_id_seq",
        allocationSize = 1)
public class PlaceCommentLike {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "PLACE_COMMENT_LIKE_ID_SEQ_GENERATOR"
    )
    private Long id;

    private Long placeCommentId;

    private Long memberId;

    public PlaceCommentLike(Long placeCommentId, Long memberId) {
        this.placeCommentId = placeCommentId;
        this.memberId = memberId;
    }

}
