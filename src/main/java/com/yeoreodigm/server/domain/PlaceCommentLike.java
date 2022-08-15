package com.yeoreodigm.server.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = @Index(name = "multiIndex1", columnList = "placeCommentId, memberId"))
public class PlaceCommentLike {

    @Id @GeneratedValue
    private Long id;

    private Long placeCommentId;

    private Long memberId;

    public PlaceCommentLike(Long placeCommentId, Long memberId) {
        this.placeCommentId = placeCommentId;
        this.memberId = memberId;
    }

}
